package com.interpark_clone.domain.reservation.service;

import com.interpark_clone.domain.concert.entity.ConcertSchedule;
import com.interpark_clone.domain.concert.entity.ConcertStatus;
import com.interpark_clone.domain.concert.repository.ConcertScheduleRepository;
import com.interpark_clone.domain.member.entity.Member;
import com.interpark_clone.domain.member.repository.MemberRepository;
import com.interpark_clone.domain.payment.entity.Payment;
import com.interpark_clone.domain.payment.entity.PaymentStatus;
import com.interpark_clone.domain.payment.repository.PaymentRepository;
import com.interpark_clone.domain.reservation.dto.SeatSectionInfo;
import com.interpark_clone.domain.reservation.dto.SectionInfo;
import com.interpark_clone.domain.reservation.dto.request.SeatHoldRequest;
import com.interpark_clone.domain.reservation.dto.request.SeatAvailabilityRequest;
import com.interpark_clone.domain.reservation.dto.response.SeatHoldResponse;
import com.interpark_clone.domain.reservation.dto.response.SeatAvailabilityResponse;
import com.interpark_clone.domain.reservation.entity.EventType;
import com.interpark_clone.domain.reservation.entity.Reservation;
import com.interpark_clone.domain.reservation.entity.SeatHold;
import com.interpark_clone.domain.reservation.repository.ReservationRepository;
import com.interpark_clone.domain.reservation.repository.SeatHoldRepository;
import com.interpark_clone.domain.sport.entity.BaseballGame;
import com.interpark_clone.domain.sport.entity.GameStatus;
import com.interpark_clone.domain.sport.repository.BaseballGameRepository;
import com.interpark_clone.domain.venue.entity.Seat;
import com.interpark_clone.domain.venue.repository.SeatRepository;
import com.interpark_clone.global.code.BusinessErrorCode;
import com.interpark_clone.global.code.GeneralErrorCode;
import com.interpark_clone.global.exception.BusinessException;
import com.interpark_clone.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final SeatHoldRepository seatHoldRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final ConcertScheduleRepository concertScheduleRepository;
    private final BaseballGameRepository baseballGameRepository;

    @Transactional(readOnly = true)
    public SeatAvailabilityResponse getSeatAvailability(SeatAvailabilityRequest request) {

        // 예매 가능한 이벤트인지 검증
        validateEvent(request.eventType(), request.eventRefId(), LocalDateTime.now());

        // 전체 좌석 조회
        List<SeatSectionInfo> seats = seatRepository.findSeatSectionInfos(request.sectionIds());

        // 점유 중인 좌석 조회
        LocalDateTime now = LocalDateTime.now();
        Set<Long> unavailableSeatIds = seatHoldRepository.findUnavailableSeats(
                request.eventType(),
                request.eventRefId(),
                request.sectionIds(),
                now
        );

        // 섹션별 좌석 정보 구성
        List<SectionInfo> sections = groupSeatsBySection(seats, unavailableSeatIds);

        return new SeatAvailabilityResponse(
                request.eventType(),
                request.eventRefId(),
                sections
        );
    }

    private List<SectionInfo> groupSeatsBySection(
            List<SeatSectionInfo> seats,
            Set<Long> unavailableSeatIds
    ) {
        return seats.stream()
                .collect(Collectors.groupingBy(
                        SeatSectionInfo::sectionId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .values()
                .stream()
                .map(sectionSeats -> SectionInfo.fromSeatSectionsAndAvailability(sectionSeats, unavailableSeatIds))
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public SeatHoldResponse createSeatHold(String memberKey, SeatHoldRequest request) {

        // 서비스 처리 시작 시각으로 예매 기간 판단: 이후 잠금 대기 중 마감되어도 허용
        LocalDateTime requestedAt = LocalDateTime.now();
        Long eventVenueId = validateEvent(request.eventType(), request.eventRefId(), requestedAt);

        // 인증된 회원 키로 예약자 조회
        Member member = memberRepository.findByMemberKey(memberKey)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MEMBER_NOT_FOUND));

        // 같은 좌석의 동시 점유를 막기 위해 트랜잭션 종료까지 pessimistic write lock 확보
        List<Seat> seats = seatRepository.findAllByIdInWithLock(request.seatIds());

        // 요청한 좌석 검증
        validateAllSeatsExist(request.seatIds(), seats);
        validateSeatsBelongToVenue(seats, eventVenueId);

        // 같은 이벤트에서 확정되었거나 아직 점유 시간이 남은 좌석은 재점유 불가
        LocalDateTime now = LocalDateTime.now();
        Set<Long> activeSeatHoldSeatIds = seatHoldRepository.findActiveSeatHoldSeatIds(
                request.eventType(),
                request.eventRefId(),
                request.seatIds(),
                now
        );
        if (!activeSeatHoldSeatIds.isEmpty()) {
            throw new BusinessException(BusinessErrorCode.SEAT_ALREADY_HELD);
        }

        // 선택한 각 좌석의 구역 가격을 합산하여 결제 금액 계산
        int totalPrice = seats.stream()
                .mapToInt(seat -> seat.getSection().getPrice())
                .sum();

        // 결제 대기(PENDING) 예약 생성: 예약 좌석도 cascade로 함께 저장
        Reservation reservation = Reservation.builder()
                .member(member)
                .eventType(request.eventType())
                .eventRefId(request.eventRefId())
                .seats(seats)
                .totalPrice(totalPrice)
                .build();
        reservationRepository.save(reservation);

        // 예약에 연결된 결제 준비(READY) 정보 생성
        Payment payment = Payment.builder()
                .reservation(reservation)
                .amount(totalPrice)
                .status(PaymentStatus.READY)
                .method(request.paymentMethod())
                .build();
        paymentRepository.save(payment);

        // 선택한 모든 좌석을 동일한 만료 시각(now + 5분)으로 임시 점유
        LocalDateTime expiresAt = SeatHold.calculateExpiresAt(now);
        List<SeatHold> seatHolds = seats.stream()
                .map(seat -> SeatHold.create(
                        seat,
                        member,
                        reservation,
                        request.eventType(),
                        request.eventRefId(),
                        expiresAt
                ))
                .toList();
        seatHoldRepository.saveAll(seatHolds);

        // 이후 결제에 사용할 예약, 결제 ID와 점유 만료 시각 반환
        return SeatHoldResponse.of(reservation, payment, expiresAt);
    }

    private Long validateEvent(EventType eventType, Long eventRefId, LocalDateTime requestedAt) {
        return switch (eventType) {
            case CONCERT -> {
                ConcertSchedule schedule = concertScheduleRepository.findById(eventRefId)
                        .orElseThrow(() -> new BusinessException(BusinessErrorCode.EVENT_SCHEDULE_NOT_FOUND));
                if ((schedule.getStatus() != ConcertStatus.OPEN && schedule.getStatus() != ConcertStatus.UPCOMING)
                        || !isWithinBookingPeriod(requestedAt, schedule.getOpenAt(), schedule.getCloseAt())) {
                    throw new BusinessException(BusinessErrorCode.EVENT_NOT_RESERVABLE);
                }
                yield schedule.getVenue().getId();
            }
            case BASEBALL -> {
                BaseballGame game = baseballGameRepository.findById(eventRefId)
                        .orElseThrow(() -> new BusinessException(BusinessErrorCode.EVENT_SCHEDULE_NOT_FOUND));
                if ((game.getStatus() != GameStatus.OPEN && game.getStatus() != GameStatus.SCHEDULED)
                        || !isWithinBookingPeriod(requestedAt, game.getOpenAt(), game.getCloseAt())) {
                    throw new BusinessException(BusinessErrorCode.EVENT_NOT_RESERVABLE);
                }
                yield game.getVenue().getId();
            }
            case EXHIBITION -> throw new GeneralException(GeneralErrorCode.INVALID_REQUEST_PARAMETER);
        };
    }

    private boolean isWithinBookingPeriod(LocalDateTime now, LocalDateTime openAt, LocalDateTime closeAt) {
        return openAt != null && closeAt != null
                && !now.isBefore(openAt) && now.isBefore(closeAt);
    }

    private void validateAllSeatsExist(List<Long> requestedSeatIds, List<Seat> seats) {
        if (requestedSeatIds.size() != seats.size()) {
            throw new BusinessException(BusinessErrorCode.SEAT_NOT_FOUND);
        }
    }

    private void validateSeatsBelongToVenue(List<Seat> seats, Long eventVenueId) {
        boolean hasInvalidSeat = seats.stream()
                .anyMatch(seat -> !seat.getSection().getVenue().getId().equals(eventVenueId));

        if (hasInvalidSeat) {
            throw new BusinessException(BusinessErrorCode.SEAT_VENUE_MISMATCH);
        }
    }
}

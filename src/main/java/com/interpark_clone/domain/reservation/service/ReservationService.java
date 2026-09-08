package com.interpark_clone.domain.reservation.service;

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
import com.interpark_clone.domain.sport.repository.BaseballGameRepository;
import com.interpark_clone.domain.venue.entity.Seat;
import com.interpark_clone.domain.venue.repository.SeatRepository;
import com.interpark_clone.global.code.BusinessErrorCode;
import com.interpark_clone.global.code.GeneralErrorCode;
import com.interpark_clone.global.exception.BusinessException;
import com.interpark_clone.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    @Transactional
    public SeatHoldResponse createSeatHold(String memberKey, SeatHoldRequest request) {
        Long eventVenueId = getEventVenueId(request.eventType(), request.eventRefId());

        Member member = memberRepository.findByMemberKey(memberKey)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MEMBER_NOT_FOUND));

        List<Seat> seats = seatRepository.findAllByIdInWithLock(request.seatIds());
        validateAllSeatsExist(request.seatIds(), seats);
        validateSeatsBelongToVenue(seats, eventVenueId);

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

        int totalPrice = seats.stream()
                .mapToInt(seat -> seat.getSection().getPrice())
                .sum();

        Reservation reservation = Reservation.builder()
                .member(member)
                .eventType(request.eventType())
                .eventRefId(request.eventRefId())
                .seats(seats)
                .totalPrice(totalPrice)
                .build();
        reservationRepository.save(reservation);

        Payment payment = Payment.builder()
                .reservation(reservation)
                .amount(totalPrice)
                .status(PaymentStatus.READY)
                .method(request.paymentMethod())
                .build();
        paymentRepository.save(payment);

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

        return SeatHoldResponse.of(reservation, payment, expiresAt);
    }

    private Long getEventVenueId(EventType eventType, Long eventRefId) {
        return switch (eventType) {
            case CONCERT -> concertScheduleRepository.findVenueIdById(eventRefId)
                    .orElseThrow(() -> new BusinessException(BusinessErrorCode.EVENT_SCHEDULE_NOT_FOUND));
            case BASEBALL -> baseballGameRepository.findVenueIdById(eventRefId)
                    .orElseThrow(() -> new BusinessException(BusinessErrorCode.EVENT_SCHEDULE_NOT_FOUND));
            case EXHIBITION -> throw new GeneralException(GeneralErrorCode.INVALID_REQUEST_PARAMETER);
        };
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

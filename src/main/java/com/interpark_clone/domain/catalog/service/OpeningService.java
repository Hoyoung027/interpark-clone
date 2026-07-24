package com.interpark_clone.domain.catalog.service;

import com.interpark_clone.domain.concert.entity.ConcertStatus;
import com.interpark_clone.domain.concert.repository.ConcertScheduleRepository;
import com.interpark_clone.domain.exhibition.entity.ExhibitionStatus;
import com.interpark_clone.domain.exhibition.repository.ExhibitionRepository;
import com.interpark_clone.domain.catalog.dto.request.OpeningRequest;
import com.interpark_clone.domain.catalog.dto.response.OpeningResponse;
import com.interpark_clone.domain.venue.entity.City;
import com.interpark_clone.global.code.GeneralErrorCode;
import com.interpark_clone.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OpeningService {

    private final ConcertScheduleRepository concertScheduleRepository;
    private final ExhibitionRepository exhibitionRepository;

    @Transactional(readOnly = true)
    public Page<OpeningResponse> getUpcomingOpenings(OpeningRequest request) {

        // 조회 기간 가공
        LocalDate from = request.fromValue();
        LocalDate to = request.toValue();

        // 조회 기간 검증
        if (to.isBefore(from)) {
            throw new GeneralException(GeneralErrorCode.INVALID_REQUEST_PARAMETER);
        }

        // 날짜 기준을 시간 범위로 변환
        LocalDateTime fromAt = from.atStartOfDay();
        LocalDateTime toAt = to.plusDays(1).atStartOfDay().minusNanos(1);

        // 타입별 오픈 예정 목록 조회
        List<OpeningItem> openings = switch (request.typeValue()) {
            case ALL -> Stream.concat(
                            getConcertOpenings(fromAt, toAt, request.region()).stream(),
                            getExhibitionOpenings(fromAt, toAt, request.region()).stream()
                    )
                    .toList();
            case CONCERT -> getConcertOpenings(fromAt, toAt, request.region());
            case EXHIBITION -> getExhibitionOpenings(fromAt, toAt, request.region());
        };

        // 정렬 기준 적용
        List<OpeningResponse> sortedOpenings = openings.stream()
                .sorted(openingComparator(request.sortValue()))
                .map(OpeningItem::response)
                .toList();

        // 메모리 페이지네이션 적용
        Pageable pageable = PageRequest.of(request.pageValue(), request.sizeValue());
        int fromIndex = Math.min((int) pageable.getOffset(), sortedOpenings.size());
        int toIndex = Math.min(fromIndex + pageable.getPageSize(), sortedOpenings.size());

        return new PageImpl<>(
                sortedOpenings.subList(fromIndex, toIndex),
                pageable,
                sortedOpenings.size()
        );
    }

    private List<OpeningItem> getConcertOpenings(LocalDateTime fromAt, LocalDateTime toAt, City region) {

        // 오픈 예정 콘서트 회차 조회
        return concertScheduleRepository.findUpcomingSchedules(
                        fromAt,
                        toAt,
                        ConcertStatus.UPCOMING,
                        region
                )
                .stream()
                .map(schedule -> new OpeningItem(
                        OpeningResponse.fromConcertSchedule(schedule),
                        schedule.getOpenAt(),
                        schedule.getConcert().getViewCount(),
                        schedule.getConcert().getCreatedAt()
                ))
                .toList();
    }

    private List<OpeningItem> getExhibitionOpenings(LocalDateTime fromAt, LocalDateTime toAt, City region) {

        // 오픈 예정 전시 조회
        return exhibitionRepository.findUpcomingExhibitions(
                        fromAt,
                        toAt,
                        ExhibitionStatus.UPCOMING,
                        region
                )
                .stream()
                .map(exhibition -> new OpeningItem(
                        OpeningResponse.fromExhibition(exhibition),
                        exhibition.getOpenAt(),
                        exhibition.getViewCount(),
                        exhibition.getCreatedAt()
                ))
                .toList();
    }

    private Comparator<OpeningItem> openingComparator(String sort) {

        // 정렬 조건 생성
        return switch (sort) {
            case "openAt" -> Comparator.comparing(OpeningItem::openAt);
            case "latest" -> Comparator.comparing(OpeningItem::createdAt, Comparator.nullsLast(Comparator.naturalOrder()))
                    .reversed();
            case "viewCount" -> Comparator.comparing(OpeningItem::viewCount, Comparator.nullsLast(Comparator.naturalOrder()))
                    .reversed();
            default -> throw new GeneralException(GeneralErrorCode.INVALID_REQUEST_PARAMETER);
        };
    }

    private record OpeningItem(
            OpeningResponse response,
            LocalDateTime openAt,
            Integer viewCount,
            LocalDateTime createdAt
    ) {
    }
}

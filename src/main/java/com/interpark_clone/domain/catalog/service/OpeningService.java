package com.interpark_clone.domain.catalog.service;

import com.interpark_clone.domain.concert.entity.ConcertStatus;
import com.interpark_clone.domain.concert.repository.ConcertScheduleRepository;
import com.interpark_clone.domain.exhibition.entity.ExhibitionStatus;
import com.interpark_clone.domain.exhibition.repository.ExhibitionRepository;
import com.interpark_clone.domain.catalog.dto.request.OpeningRequest;
import com.interpark_clone.domain.catalog.dto.response.OpeningResponse;
import com.interpark_clone.domain.venue.entity.City;
import com.interpark_clone.global.code.GeneralErrorCode;
import com.interpark_clone.global.enums.SortType;
import com.interpark_clone.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class OpeningService {

    private final ConcertScheduleRepository concertScheduleRepository;
    private final ExhibitionRepository exhibitionRepository;

    @Transactional(readOnly = true)
    public Page<OpeningResponse> getUpcomingOpenings(OpeningRequest request) {

        // 날짜 기준을 시간 범위로 변환
        LocalDateTime fromAt = request.from().atStartOfDay();
        LocalDateTime toAt = request.to().plusDays(1).atStartOfDay().minusNanos(1);

        // 타입별 오픈 예정 목록 조회
        Pageable pageable = PageRequest.of(request.page(), request.size());
        Page<OpeningResponse> result = switch (request.genre()) {
            case CONCERT -> getConcertOpenings(fromAt, toAt, request.region(), request.sort(), pageable);
            case EXHIBITION -> getExhibitionOpenings(fromAt, toAt, request.region(), request.sort(), pageable);
            case ALL -> throw new GeneralException(GeneralErrorCode.INVALID_REQUEST_PARAMETER);
        };

        return result;
    }

    private Page<OpeningResponse> getConcertOpenings(LocalDateTime from, LocalDateTime to, City region, SortType sort, Pageable pageable) {

        // 오픈 예정 콘서트 회차 조회
        return concertScheduleRepository.findUpcomingSchedules(
                        from,
                        to,
                        ConcertStatus.UPCOMING,
                        region,
                        sort.name(),
                        pageable
                )
                .map(OpeningResponse::fromConcertSchedule);
    }

    private Page<OpeningResponse> getExhibitionOpenings(LocalDateTime from, LocalDateTime to, City region, SortType sort, Pageable pageable) {

        // 오픈 예정 전시 조회
        return exhibitionRepository.findUpcomingExhibitions(
                        from,
                        to,
                        ExhibitionStatus.UPCOMING,
                        region,
                        sort.name(),
                        pageable
                )
                .map(OpeningResponse::fromExhibition);
    }
}

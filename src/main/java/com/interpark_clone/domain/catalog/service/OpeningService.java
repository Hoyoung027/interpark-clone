package com.interpark_clone.domain.catalog.service;

import com.interpark_clone.domain.concert.entity.ConcertStatus;
import com.interpark_clone.domain.concert.repository.ConcertScheduleRepository;
import com.interpark_clone.domain.exhibition.entity.ExhibitionStatus;
import com.interpark_clone.domain.exhibition.repository.ExhibitionRepository;
import com.interpark_clone.domain.catalog.dto.request.OpeningRequest;
import com.interpark_clone.domain.catalog.dto.response.OpeningResponse;
import com.interpark_clone.domain.venue.entity.City;
import com.interpark_clone.global.code.BusinessErrorCode;
import com.interpark_clone.global.code.GeneralErrorCode;
import com.interpark_clone.global.enums.SortType;
import com.interpark_clone.global.exception.BusinessException;
import com.interpark_clone.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OpeningService {

    private final ConcertScheduleRepository concertScheduleRepository;
    private final ExhibitionRepository exhibitionRepository;

    @Transactional(readOnly = true)
    public Page<OpeningResponse> getUpcomingOpenings(OpeningRequest request) {

        // DTO 가공
        LocalDate from = request.fromValue();
        LocalDate to = request.toValue();

        // 조회 기간 검증
        if (to.isBefore(from)) {
            throw new GeneralException(GeneralErrorCode.INVALID_REQUEST_PARAMETER);
        }
        if (!request.isValidPeriod()) {
            throw new BusinessException(BusinessErrorCode.DATE_RANGE_TOO_LARGE);
        }

        // 정렬 기준 검증
        if (!request.isValidSort()) {
            throw new GeneralException(GeneralErrorCode.INVALID_REQUEST_PARAMETER);
        }

        // 날짜 기준을 시간 범위로 변환
        LocalDateTime fromAt = from.atStartOfDay();
        LocalDateTime toAt = to.plusDays(1).atStartOfDay().minusNanos(1);

        // 타입별 오픈 예정 목록 조회
        Pageable pageable = PageRequest.of(request.pageValue(), request.sizeValue());
        Page<OpeningResponse> result = switch (request.genreValue()) {
            case CONCERT -> getConcertOpenings(fromAt, toAt, request.region(), request.sortValue(), pageable);
            case EXHIBITION -> getExhibitionOpenings(fromAt, toAt, request.region(), request.sortValue(), pageable);
            case ALL -> throw new GeneralException(GeneralErrorCode.INVALID_REQUEST_PARAMETER);
        };

        return result;
    }

    private Page<OpeningResponse> getConcertOpenings(LocalDateTime fromAt, LocalDateTime toAt, City region, SortType sort, Pageable pageable) {

        // 오픈 예정 콘서트 회차 조회
        return concertScheduleRepository.findUpcomingSchedules(
                        fromAt,
                        toAt,
                        ConcertStatus.UPCOMING,
                        region,
                        sort,
                        pageable
                )
                .map(OpeningResponse::fromConcertSchedule);
    }

    private Page<OpeningResponse> getExhibitionOpenings(LocalDateTime fromAt, LocalDateTime toAt, City region, SortType sort, Pageable pageable) {

        // 오픈 예정 전시 조회
        return exhibitionRepository.findUpcomingExhibitions(
                        fromAt,
                        toAt,
                        ExhibitionStatus.UPCOMING,
                        region,
                        sort,
                        pageable
                )
                .map(OpeningResponse::fromExhibition);
    }
}

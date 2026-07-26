package com.interpark_clone.domain.concert.service;

import com.interpark_clone.domain.concert.dto.response.ConcertClipResponse;
import com.interpark_clone.domain.concert.dto.request.ConcertClipRequest;
import com.interpark_clone.domain.concert.dto.response.ConcertDetailResponse;
import com.interpark_clone.domain.concert.dto.response.ConcertRankingItemResponse;
import com.interpark_clone.domain.concert.dto.response.ConcertRankingResponse;
import com.interpark_clone.domain.concert.dto.request.ConcertRankingRequest;
import com.interpark_clone.domain.concert.dto.request.ConcertRequest;
import com.interpark_clone.domain.concert.dto.response.ConcertResponse;
import com.interpark_clone.domain.concert.entity.Concert;
import com.interpark_clone.domain.concert.entity.ConcertSchedule;
import com.interpark_clone.domain.concert.repository.ConcertRepository;
import com.interpark_clone.domain.concert.repository.ConcertScheduleRepository;
import com.interpark_clone.global.code.BusinessErrorCode;
import com.interpark_clone.global.code.GeneralErrorCode;
import com.interpark_clone.global.exception.BusinessException;
import com.interpark_clone.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final ConcertScheduleRepository concertScheduleRepository;

    @Transactional(readOnly = true)
    public Page<ConcertResponse> getConcertList(ConcertRequest request) {
        Pageable pageable = PageRequest.of(request.pageValue(), request.sizeValue());

        return switch (request.normalizedSort()) {
            // 랭킹순 정렬
            case RESERVATION -> {
                LocalDate today = LocalDate.now();
                yield concertRepository.findConcertsOrderByRanking(
                        request.genre(),
                        request.region(),
                        today.atStartOfDay(),
                        today.plusDays(1).atStartOfDay(),
                        pageable
                );
            }
            // 공연 종료 임박순
            case CLOSING_SOON -> concertRepository.findConcertsOrderByClosingSoon(
                    request.genre(),
                    request.region(),
                    pageable
            );
            default -> throw new GeneralException(GeneralErrorCode.INVALID_REQUEST_PARAMETER);
        };
    }

    @Transactional(readOnly = true)
    public Page<ConcertClipResponse> getConcertClips(ConcertClipRequest request) {

        // 콘서트 클립 반환
        Pageable pageable = PageRequest.of(request.pageValue(), request.sizeValue());
        LocalDate today = LocalDate.now();

        Page<ConcertClipResponse> clips = concertRepository.findConcertClipsOrderByRanking(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay(),
                pageable
        );

        return clips;
    }

    @Transactional
    public ConcertDetailResponse getConcert(Long concertId) {

        // 콘서트 정보 조회
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.CONCERT_NOT_FOUND));

        concert.increaseViewCount();

        // 콘서트 회차 정보 조회
        List<ConcertSchedule> schedule = concertScheduleRepository.findByConcertIdWithVenue(concertId);

        return ConcertDetailResponse.of(
                concert,
                schedule
        );
    }

    @Transactional(readOnly = true)
    public Page<ConcertRankingResponse> getDailyRankings(ConcertRankingRequest request) {

        // DTO 형식 맞춤
        LocalDate targetDate = request.rankingDateValue();

        // 랭킹순 공연 반환
        Pageable pageable = PageRequest.of(request.pageValue(), request.sizeValue());
        Page<ConcertRankingItemResponse> rankingItems = concertRepository.findDailyRankingItems(
                request.genre(),
                targetDate.atStartOfDay(),
                targetDate.plusDays(1).atStartOfDay(),
                pageable
        );

        // 랭킹 순위 추가
        int rankOffset = rankingItems.getNumber() * rankingItems.getSize();
        List<ConcertRankingResponse> content = IntStream.range(0, rankingItems.getContent().size())
                .mapToObj(index -> ConcertRankingResponse.of(
                        rankOffset + index + 1,
                        rankingItems.getContent().get(index)
                ))
                .toList();

        return new PageImpl<>(content, pageable, rankingItems.getTotalElements());
    }
}

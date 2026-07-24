package com.interpark_clone.domain.exhibition.service;

import com.interpark_clone.domain.exhibition.dto.request.ExhibitionRankingRequest;
import com.interpark_clone.domain.exhibition.dto.request.ExhibitionRequest;
import com.interpark_clone.domain.exhibition.dto.request.RegionalExhibitionRequest;
import com.interpark_clone.domain.exhibition.dto.response.ExhibitionRankingItemResponse;
import com.interpark_clone.domain.exhibition.dto.response.ExhibitionRankingResponse;
import com.interpark_clone.domain.exhibition.dto.response.ExhibitionResponse;
import com.interpark_clone.domain.exhibition.repository.ExhibitionRepository;
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
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;

    @Transactional(readOnly = true)
    public Page<ExhibitionResponse> getExhibitions(ExhibitionRequest request) {

        // 페이지 요청 정보 생성
        Pageable pageable = PageRequest.of(request.pageValue(), request.sizeValue());

        return switch (request.normalizedSort()) {
            // 랭킹순 정렬
            case "ranking" -> {
                LocalDate today = LocalDate.now();
                yield exhibitionRepository.findExhibitionsOrderByRanking(
                        request.genre(),
                        request.region(),
                        today.atStartOfDay(),
                        today.plusDays(1).atStartOfDay(),
                        pageable
                );
            }
            // 전시 종료 임박순
            case "closingSoon" -> exhibitionRepository.findExhibitionsOrderByClosingSoon(
                    request.genre(),
                    request.region(),
                    pageable
            );
            default -> throw new GeneralException(GeneralErrorCode.INVALID_REQUEST_PARAMETER);
        };
    }

    @Transactional(readOnly = true)
    public Page<ExhibitionResponse> getRegionalExhibitions(RegionalExhibitionRequest request) {

        // 지역별 전시 랭킹 반환
        Pageable pageable = PageRequest.of(request.pageValue(), request.sizeValue());
        LocalDate today = LocalDate.now();

        return exhibitionRepository.findExhibitionsOrderByRanking(
                null,
                request.region(),
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay(),
                pageable
        );
    }

    @Transactional(readOnly = true)
    public Page<ExhibitionRankingResponse> getDailyRankings(ExhibitionRankingRequest request) {

        // DTO 형식 맞춤
        LocalDate targetDate = request.rankingDateValue();

        // 랭킹순 전시 반환
        Pageable pageable = PageRequest.of(request.pageValue(), request.sizeValue());
        Page<ExhibitionRankingItemResponse> rankingItems = exhibitionRepository.findDailyRankingItems(
                request.genre(),
                targetDate.atStartOfDay(),
                targetDate.plusDays(1).atStartOfDay(),
                pageable
        );

        // 랭킹 순위 추가
        int rankOffset = rankingItems.getNumber() * rankingItems.getSize();
        List<ExhibitionRankingResponse> content = IntStream.range(0, rankingItems.getContent().size())
                .mapToObj(index -> ExhibitionRankingResponse.of(
                        rankOffset + index + 1,
                        rankingItems.getContent().get(index)
                ))
                .toList();

        return new PageImpl<>(content, pageable, rankingItems.getTotalElements());
    }
}

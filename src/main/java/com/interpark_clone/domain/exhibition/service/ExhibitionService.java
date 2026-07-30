package com.interpark_clone.domain.exhibition.service;

import com.interpark_clone.domain.exhibition.dto.request.ExhibitionRankingRequest;
import com.interpark_clone.domain.exhibition.dto.request.ExhibitionRequest;
import com.interpark_clone.domain.exhibition.dto.request.RegionalExhibitionRequest;
import com.interpark_clone.domain.exhibition.dto.response.ExhibitionRankingItemResponse;
import com.interpark_clone.domain.exhibition.dto.response.ExhibitionRankingResponse;
import com.interpark_clone.domain.exhibition.dto.response.ExhibitionDetailResponse;
import com.interpark_clone.domain.exhibition.dto.response.ExhibitionResponse;
import com.interpark_clone.domain.exhibition.entity.Exhibition;
import com.interpark_clone.domain.exhibition.repository.ExhibitionRepository;
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
public class ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;

    @Transactional
    public ExhibitionDetailResponse getExhibition(Long exhibitionId) {

        // 전시 정보 조회
        Exhibition exhibition = exhibitionRepository.findByIdWithVenue(exhibitionId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.EXHIBITION_NOT_FOUND));

        // 조회수 증가
        exhibitionRepository.increaseViewCount(exhibitionId);

        return ExhibitionDetailResponse.from(exhibition);
    }

    @Transactional(readOnly = true)
    public Page<ExhibitionResponse> getExhibitions(ExhibitionRequest request) {

        // 페이지 요청 정보 생성
        Pageable pageable = PageRequest.of(request.page(), request.size());

        return switch (request.sort()) {
            // 랭킹순 정렬
            case RESERVATION -> {
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
            case CLOSING_SOON -> exhibitionRepository.findExhibitionsOrderByClosingSoon(
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
        Pageable pageable = PageRequest.of(request.page(), request.size());
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

        // 랭킹순 전시 반환
        Pageable pageable = PageRequest.of(request.page(), request.size());
        Page<ExhibitionRankingItemResponse> rankingItems = exhibitionRepository.findDailyRankingItems(
                request.genre(),
                request.rankingDate().atStartOfDay(),
                request.rankingDate().plusDays(1).atStartOfDay(),
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

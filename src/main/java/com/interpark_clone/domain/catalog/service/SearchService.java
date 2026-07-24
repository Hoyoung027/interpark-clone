package com.interpark_clone.domain.catalog.service;

import com.interpark_clone.domain.catalog.dto.SearchSaleStatus;
import com.interpark_clone.domain.catalog.dto.request.SearchRequest;
import com.interpark_clone.domain.catalog.dto.response.SearchResponse;
import com.interpark_clone.domain.concert.dto.response.ConcertResponse;
import com.interpark_clone.domain.concert.entity.ConcertStatus;
import com.interpark_clone.domain.concert.repository.ConcertRepository;
import com.interpark_clone.domain.exhibition.dto.response.ExhibitionResponse;
import com.interpark_clone.domain.exhibition.entity.ExhibitionStatus;
import com.interpark_clone.domain.exhibition.repository.ExhibitionRepository;
import com.interpark_clone.global.code.GeneralErrorCode;
import com.interpark_clone.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ConcertRepository concertRepository;
    private final ExhibitionRepository exhibitionRepository;

    @Transactional(readOnly = true)
    public SearchResponse search(SearchRequest request) {

        // DTO 가공
        String keyword = request.normalizedKeyword();
        List<SearchSaleStatus> saleStatuses = request.saleStatusValues();
        String sort = request.normalizedSort();
        validateSort(sort);

        // 콘서트 및 전시 조회
        Pageable pageable = PageRequest.of(request.pageValue(), request.sizeValue());
        Page<ConcertResponse> concerts = switch (request.typeValue()) {
            case ALL, CONCERT -> concertRepository.searchConcerts(
                    keyword,
                    concertStatuses(saleStatuses),
                    request.region(),
                    sort,
                    pageable
            );
            case EXHIBITION -> Page.empty(pageable);
        };
        Page<ExhibitionResponse> exhibitions = switch (request.typeValue()) {
            case ALL, EXHIBITION -> exhibitionRepository.searchExhibitions(
                    keyword,
                    exhibitionStatuses(saleStatuses),
                    request.region(),
                    sort,
                    pageable
            );
            case CONCERT -> Page.empty(pageable);
        };

        return SearchResponse.of(concerts, exhibitions);
    }

    private List<ConcertStatus> concertStatuses(List<SearchSaleStatus> saleStatuses) {
        return saleStatuses.stream()
                .map(status -> switch (status) {
                    case UPCOMING -> ConcertStatus.UPCOMING;
                    case OPEN -> ConcertStatus.OPEN;
                    case CLOSED -> ConcertStatus.CLOSED;
                })
                .distinct()
                .toList();
    }

    private List<ExhibitionStatus> exhibitionStatuses(List<SearchSaleStatus> saleStatuses) {
        return saleStatuses.stream()
                .map(status -> switch (status) {
                    case UPCOMING -> ExhibitionStatus.UPCOMING;
                    case OPEN -> ExhibitionStatus.OPEN;
                    case CLOSED -> ExhibitionStatus.CLOSED;
                })
                .distinct()
                .toList();
    }

    private void validateSort(String sort) {
        switch (sort) {
            case "ranking", "reservationCount", "closingSoon", "latest" -> {
            }
            default -> throw new GeneralException(GeneralErrorCode.INVALID_REQUEST_PARAMETER);
        }
    }
}

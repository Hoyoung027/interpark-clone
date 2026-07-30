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

        // 콘서트 및 전시 조회
        Pageable pageable = PageRequest.of(request.page(), request.size());
        Page<ConcertResponse> concerts = switch (request.genre()) {
            case ALL, CONCERT -> concertRepository.searchConcerts(
                    request.keyword(),
                    concertStatuses(request.saleStatuses()),
                    request.region(),
                    request.sort().name(),
                    pageable
            );
            case EXHIBITION -> Page.empty(pageable);
        };
        Page<ExhibitionResponse> exhibitions = switch (request.genre()) {
            case ALL, EXHIBITION -> exhibitionRepository.searchExhibitions(
                    request.keyword(),
                    exhibitionStatuses(request.saleStatuses()),
                    request.region(),
                    request.sort().name(),
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
}

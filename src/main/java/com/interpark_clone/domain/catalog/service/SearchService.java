package com.interpark_clone.domain.catalog.service;

import com.interpark_clone.domain.catalog.dto.request.SearchRequest;
import com.interpark_clone.domain.catalog.dto.response.SearchResponse;
import com.interpark_clone.domain.concert.dto.response.ConcertResponse;
import com.interpark_clone.domain.concert.repository.ConcertRepository;
import com.interpark_clone.domain.exhibition.dto.response.ExhibitionResponse;
import com.interpark_clone.domain.exhibition.repository.ExhibitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ConcertRepository concertRepository;
    private final ExhibitionRepository exhibitionRepository;

    @Transactional(readOnly = true)
    public SearchResponse search(SearchRequest request) {

        // DTO 가공
        String keyword = request.normalizedKeyword();

        // 콘서트 및 전시 조회
        Pageable pageable = PageRequest.of(request.pageValue(), request.sizeValue());
        Page<ConcertResponse> concerts = concertRepository.searchConcerts(keyword, pageable);
        Page<ExhibitionResponse> exhibitions = exhibitionRepository.searchExhibitions(keyword, pageable);

        return SearchResponse.of(concerts, exhibitions);
    }
}

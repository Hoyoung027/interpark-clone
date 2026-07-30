package com.interpark_clone.domain.catalog.dto.response;

import com.interpark_clone.domain.concert.dto.response.ConcertResponse;
import com.interpark_clone.domain.exhibition.dto.response.ExhibitionResponse;
import com.interpark_clone.global.response.PageInfo;
import org.springframework.data.domain.Page;

import java.util.List;

public record SearchResponse(
        SearchSection<ConcertResponse> concerts,
        SearchSection<ExhibitionResponse> exhibitions
) {
    public static SearchResponse of(Page<ConcertResponse> concerts, Page<ExhibitionResponse> exhibitions) {
        return new SearchResponse(SearchSection.from(concerts), SearchSection.from(exhibitions));
    }

    public record SearchSection<T>(PageInfo pageInfo, List<T> items) {
        public static <T> SearchSection<T> from(Page<T> page) {
            PageInfo pageInfo = new PageInfo(
                    page.getNumber(),
                    page.getSize(),
                    page.hasNext(),
                    page.getTotalElements(),
                    page.getTotalPages()
            );
            return new SearchSection<>(pageInfo, page.getContent());
        }
    }
}

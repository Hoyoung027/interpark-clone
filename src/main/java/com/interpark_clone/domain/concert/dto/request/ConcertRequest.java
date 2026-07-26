package com.interpark_clone.domain.concert.dto.request;

import com.interpark_clone.domain.concert.entity.ConcertGenre;
import com.interpark_clone.domain.venue.entity.City;
import com.interpark_clone.global.enums.SortType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
public record ConcertRequest(
        ConcertGenre genre,
        City region,
        SortType sort,
        @Min(value = 0, message = "page는 0 이상이어야 합니다.")
        Integer page,
        @Min(value = 1, message = "size는 1 이상이어야 합니다.")
        @Max(value = 100, message = "size는 100 이하여야 합니다.")
        Integer size
) {
    private static final SortType DEFAULT_SORT = SortType.RESERVATION;
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    public SortType normalizedSort() {
        if (sort == null) {
            return DEFAULT_SORT;
        }
        return sort;
    }

    public int pageValue() {
        if (page == null) {
            return DEFAULT_PAGE;
        }
        return page;
    }

    public int sizeValue() {
        if (size == null) {
            return DEFAULT_SIZE;
        }
        return size;
    }
}

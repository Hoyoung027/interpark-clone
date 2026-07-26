package com.interpark_clone.domain.catalog.dto.request;

import com.interpark_clone.domain.catalog.dto.SearchSaleStatus;
import com.interpark_clone.domain.venue.entity.City;
import com.interpark_clone.global.enums.Genre;
import com.interpark_clone.global.enums.SortType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springdoc.core.annotations.ParameterObject;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@ParameterObject
public record SearchRequest(
        @NotBlank(message = "keyword는 필수입니다.")
        String keyword,
        Genre genre,
        List<SearchSaleStatus> saleStatuses,
        City region,
        SortType sort,
        @Min(value = 0, message = "page는 0 이상이어야 합니다.")
        Integer page,
        @Min(value = 1, message = "size는 1 이상이어야 합니다.")
        @Max(value = 100, message = "size는 100 이하여야 합니다.")
        Integer size
) {
    private static final Genre DEFAULT_GENRE = Genre.ALL;
    private static final List<SearchSaleStatus> DEFAULT_SALE_STATUSES = List.of(
            SearchSaleStatus.UPCOMING,
            SearchSaleStatus.OPEN
    );
    private static final SortType DEFAULT_SORT = SortType.VIEW;
    private static final Set<SortType> ALLOWED_SORTS = EnumSet.of(
            SortType.VIEW, SortType.RESERVATION, SortType.CLOSING_SOON, SortType.LATEST
    );
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    public String normalizedKeyword() {
        return keyword.trim();
    }

    public Genre genreValue() {
        if (genre == null) {
            return DEFAULT_GENRE;
        }
        return genre;
    }

    public List<SearchSaleStatus> saleStatusValues() {
        if (saleStatuses == null || saleStatuses.isEmpty()) {
            return DEFAULT_SALE_STATUSES;
        }
        return saleStatuses;
    }

    public SortType sortValue() {
        if (sort == null) {
            return DEFAULT_SORT;
        }
        return sort;
    }

    public boolean isValidSort() {
        return ALLOWED_SORTS.contains(sortValue());
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

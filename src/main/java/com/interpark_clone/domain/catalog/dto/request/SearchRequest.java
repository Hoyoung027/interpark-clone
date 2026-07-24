package com.interpark_clone.domain.catalog.dto.request;

import com.interpark_clone.domain.catalog.dto.OpeningType;
import com.interpark_clone.domain.catalog.dto.SearchSaleStatus;
import com.interpark_clone.domain.venue.entity.City;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springdoc.core.annotations.ParameterObject;

import java.util.List;

@ParameterObject
public record SearchRequest(
        @NotBlank(message = "keyword는 필수입니다.")
        String keyword,
        OpeningType type,
        List<SearchSaleStatus> saleStatuses,
        City region,
        String sort,
        @Min(value = 0, message = "page는 0 이상이어야 합니다.")
        Integer page,
        @Min(value = 1, message = "size는 1 이상이어야 합니다.")
        @Max(value = 100, message = "size는 100 이하여야 합니다.")
        Integer size
) {
    private static final OpeningType DEFAULT_TYPE = OpeningType.ALL;
    private static final List<SearchSaleStatus> DEFAULT_SALE_STATUSES = List.of(
            SearchSaleStatus.UPCOMING,
            SearchSaleStatus.OPEN
    );
    private static final String DEFAULT_SORT = "ranking";
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    public String normalizedKeyword() {
        return keyword.trim();
    }

    public OpeningType typeValue() {
        if (type == null) {
            return DEFAULT_TYPE;
        }
        return type;
    }

    public List<SearchSaleStatus> saleStatusValues() {
        if (saleStatuses == null || saleStatuses.isEmpty()) {
            return DEFAULT_SALE_STATUSES;
        }
        return saleStatuses;
    }

    public String normalizedSort() {
        if (sort == null || sort.isBlank()) {
            return DEFAULT_SORT;
        }
        return sort.trim();
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

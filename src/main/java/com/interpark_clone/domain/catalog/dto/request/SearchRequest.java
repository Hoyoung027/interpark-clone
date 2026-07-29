package com.interpark_clone.domain.catalog.dto.request;

import com.interpark_clone.domain.catalog.dto.SearchSaleStatus;
import com.interpark_clone.domain.venue.entity.City;
import com.interpark_clone.global.enums.Genre;
import com.interpark_clone.global.enums.SortType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springdoc.core.annotations.ParameterObject;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@ParameterObject
public record SearchRequest(
        @Schema(description = "검색 키워드입니다. 제목과 공연장명을 대상으로 검색합니다.", requiredMode = Schema.RequiredMode.REQUIRED, example = "페스티벌")
        @NotBlank(message = "keyword는 필수입니다.")
        String keyword,
        @Schema(description = "검색할 콘텐츠 타입입니다. 생략하면 콘서트와 전시를 모두 검색합니다.", allowableValues = {"ALL", "CONCERT", "EXHIBITION"}, defaultValue = "ALL", example = "ALL")
        Genre genre,
        @Schema(description = "판매 상태 필터입니다. 생략하면 UPCOMING, OPEN을 조회합니다.", allowableValues = {"UPCOMING", "OPEN", "CLOSED"}, defaultValue = "UPCOMING,OPEN", example = "OPEN")
        List<SearchSaleStatus> saleStatuses,
        @Schema(description = "지역 필터입니다. 생략하면 전체 지역을 조회합니다.", example = "SEOUL")
        City region,
        @Schema(description = "정렬 기준입니다. VIEW, RESERVATION, CLOSING_SOON, LATEST만 허용합니다.", allowableValues = {"VIEW", "RESERVATION", "CLOSING_SOON", "LATEST"}, defaultValue = "VIEW", example = "VIEW")
        SortType sort,
        @Schema(description = "페이지 번호입니다. 0부터 시작합니다.", defaultValue = "0", minimum = "0", example = "0")
        @Min(value = 0, message = "page는 0 이상이어야 합니다.")
        Integer page,
        @Schema(description = "페이지 크기입니다.", defaultValue = "20", minimum = "1", maximum = "100", example = "20")
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

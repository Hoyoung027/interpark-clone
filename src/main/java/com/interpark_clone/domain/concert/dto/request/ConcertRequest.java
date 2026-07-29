package com.interpark_clone.domain.concert.dto.request;

import com.interpark_clone.domain.concert.entity.ConcertGenre;
import com.interpark_clone.domain.venue.entity.City;
import com.interpark_clone.global.enums.SortType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
public record ConcertRequest(
        @Schema(description = "콘서트 장르 필터입니다. 생략하면 전체 장르를 조회합니다.", example = "FESTIVAL")
        ConcertGenre genre,
        @Schema(description = "지역 필터입니다. 생략하면 전체 지역을 조회합니다.", example = "SEOUL")
        City region,
        @Schema(description = "정렬 기준입니다. RESERVATION은 오늘 확정 예매 수 기준 랭킹순, CLOSING_SOON은 공연 종료 임박순입니다.", allowableValues = {"RESERVATION", "CLOSING_SOON"}, defaultValue = "RESERVATION", example = "RESERVATION")
        SortType sort,
        @Schema(description = "페이지 번호입니다. 0부터 시작합니다.", defaultValue = "0", minimum = "0", example = "0")
        @Min(value = 0, message = "page는 0 이상이어야 합니다.")
        Integer page,
        @Schema(description = "페이지 크기입니다.", defaultValue = "20", minimum = "1", maximum = "100", example = "20")
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

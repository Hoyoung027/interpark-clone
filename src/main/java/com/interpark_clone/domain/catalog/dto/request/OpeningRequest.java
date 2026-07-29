package com.interpark_clone.domain.catalog.dto.request;

import com.interpark_clone.global.enums.Genre;
import com.interpark_clone.domain.venue.entity.City;
import com.interpark_clone.global.enums.SortType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.Set;

@ParameterObject
public record OpeningRequest(
        @Schema(description = "조회할 콘텐츠 타입입니다. ALL은 지원하지 않습니다.", allowableValues = {"CONCERT", "EXHIBITION"}, defaultValue = "CONCERT", example = "CONCERT")
        Genre genre,
        @Schema(description = "지역 필터입니다. 생략하면 전체 지역을 조회합니다.", example = "SEOUL")
        City region,
        @Schema(description = "정렬 기준입니다. 오픈 예정 조회에서는 OPEN_AT, LATEST, VIEW만 허용합니다.", allowableValues = {"OPEN_AT", "LATEST", "VIEW"}, defaultValue = "OPEN_AT", example = "OPEN_AT")
        SortType sort,
        @Schema(description = "조회 시작일입니다. 생략하면 내일 날짜가 적용됩니다.", type = "string", format = "date", example = "2026-08-01")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate from,
        @Schema(description = "조회 종료일입니다. 생략하면 from 기준 6일 뒤까지 조회합니다. from부터 to까지 최대 120일까지 조회할 수 있습니다.", type = "string", format = "date", example = "2026-08-07")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate to,
        @Schema(description = "페이지 번호입니다. 0부터 시작합니다.", defaultValue = "0", minimum = "0", example = "0")
        @Min(value = 0, message = "page는 0 이상이어야 합니다.")
        Integer page,
        @Schema(description = "페이지 크기입니다.", defaultValue = "20", minimum = "1", maximum = "100", example = "20")
        @Min(value = 1, message = "size는 1 이상이어야 합니다.")
        @Max(value = 100, message = "size는 100 이하여야 합니다.")
        Integer size
) {
    private static final SortType DEFAULT_SORT = SortType.OPEN_AT;
    private static final Set<SortType> ALLOWED_SORTS = EnumSet.of(
            SortType.OPEN_AT, SortType.LATEST, SortType.VIEW
    );
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    public Genre genreValue() {
        if (genre == null) {
            return Genre.CONCERT;
        }
        return genre;
    }

    public LocalDate fromValue() {
        if (from == null) {
            return LocalDate.now().plusDays(1);
        }
        return from;
    }

    public LocalDate toValue() {
        if (to == null) {
            return fromValue().plusDays(6);
        }
        return to;
    }

    public boolean isValidPeriod() {
        if (ChronoUnit.DAYS.between(fromValue(), toValue()) > 120) {
            return false;
        }
        return true;
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

package com.interpark_clone.domain.catalog.dto.request;

import com.interpark_clone.global.enums.Genre;
import com.interpark_clone.domain.venue.entity.City;
import com.interpark_clone.global.enums.SortType;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
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
        @Parameter(
                description = "조회할 콘텐츠 타입입니다. ALL은 지원하지 않습니다.",
                schema = @Schema(type = "string", allowableValues = {"CONCERT", "EXHIBITION"}, defaultValue = "CONCERT")
        )
        Genre genre,
        @Parameter(
                description = "지역 필터입니다. 생략하면 전체 지역을 조회합니다.",
                schema = @Schema(type = "string", allowableValues = {
                        "SEOUL", "GYEONGGI", "INCHEON", "GYEONGNAM", "BUSAN", "GYEONGBUK", "DAEGU", "JEONNAM",
                        "GWANGJU", "JEONBUK", "CHUNGNAM", "DAEJEON", "CHUNGBUK", "GANGWON", "JEJU", "ULSAN"
                })
        )
        City region,
        @Parameter(
                description = "정렬 기준입니다. 오픈 예정 조회에서는 OPEN_AT, LATEST, VIEW만 허용합니다.",
                schema = @Schema(type = "string", allowableValues = {"OPEN_AT", "LATEST", "VIEW"}, defaultValue = "OPEN_AT")
        )
        SortType sort,
        @Schema(description = "조회 시작일입니다. 생략하면 내일 날짜가 적용됩니다.", type = "string", format = "date", example = "2026-08-01")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate from,
        @Schema(description = "조회 종료일입니다. 입력이 없을 경우 조회 시작일 기준 일주일 뒤까지 조회합니다. 최대 120일까지 조회할 수 있습니다.", type = "string", format = "date", example = "2026-08-07")
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
    private static final Genre DEFAULT_GENRE = Genre.CONCERT;
    private static final Set<SortType> ALLOWED_SORTS = EnumSet.of(
            SortType.OPEN_AT,
            SortType.LATEST,
            SortType.VIEW
    );
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final Integer LIMIT_DAYS = 120;

    public OpeningRequest {
        genre = genre == null ? DEFAULT_GENRE : genre;
        sort = sort == null ? DEFAULT_SORT : sort;

        LocalDate normalizedFrom = from == null
                ? LocalDate.now().plusDays(1)
                : from;

        from = normalizedFrom;
        to = to == null ? normalizedFrom.plusDays(6) : to;

        page = page == null ? DEFAULT_PAGE : page;
        size = size == null ? DEFAULT_SIZE : size;
    }

    @AssertTrue(message = "유효하지 않은 기간입니다.")
    @Schema(hidden = true)
    public boolean isValidDateOrder() {
        return !to.isBefore(from);
    }

    @AssertTrue(message = "조회 기간은 120일을 초과할 수 없습니다.")
    @Schema(hidden = true)
    public boolean isValidPeriod() {
        return !(ChronoUnit.DAYS.between(from, to) > LIMIT_DAYS);
    }

    @AssertTrue(message = "지원하지 않는 장르 타입입니다.")
    @Schema(hidden = true)
    public boolean isValidGenre() {
        return genre != Genre.ALL;
    }

    @AssertTrue(message = "지원하지 않는 정렬 기준입니다.")
    @Schema(hidden = true)
    public boolean isValidSort() {
        return ALLOWED_SORTS.contains(sort);
    }
}

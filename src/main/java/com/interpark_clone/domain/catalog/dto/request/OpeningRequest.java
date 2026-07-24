package com.interpark_clone.domain.catalog.dto.request;

import com.interpark_clone.domain.catalog.dto.OpeningType;
import com.interpark_clone.domain.venue.entity.City;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@ParameterObject
public record OpeningRequest(
        OpeningType type,
        City region,
        String sort,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate from,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate to,
        @Min(value = 0, message = "page는 0 이상이어야 합니다.")
        Integer page,
        @Min(value = 1, message = "size는 1 이상이어야 합니다.")
        @Max(value = 100, message = "size는 100 이하여야 합니다.")
        Integer size
) {
    private static final String DEFAULT_SORT = "openAt";
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    public OpeningType typeValue() {
        if (type == null) {
            return OpeningType.ALL;
        }
        return type;
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

    public String sortValue() {
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

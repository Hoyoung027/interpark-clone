package com.interpark_clone.domain.catalog.dto.request;

import com.interpark_clone.global.enums.Genre;
import com.interpark_clone.domain.venue.entity.City;
import com.interpark_clone.global.enums.SortType;
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
        Genre genre,
        City region,
        SortType sort,
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

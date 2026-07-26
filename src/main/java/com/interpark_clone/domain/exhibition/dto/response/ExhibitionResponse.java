package com.interpark_clone.domain.exhibition.dto.response;

import com.interpark_clone.domain.exhibition.entity.ExhibitionGenre;
import com.interpark_clone.global.enums.AgeRating;
import com.interpark_clone.global.enums.SaleType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ExhibitionResponse(
        Long exhibitionId,
        String title,
        String posterUrl,
        ExhibitionGenre genre,
        String venueName,
        LocalDateTime startDate,
        LocalDateTime endDate,
        SaleType saleType,
        AgeRating ageRating
) {
    public ExhibitionResponse(
            Long exhibitionId,
            String title,
            String posterUrl,
            ExhibitionGenre genre,
            String venueName,
            LocalDate startDate,
            LocalDate endDate,
            SaleType saleType,
            AgeRating ageRating
    ) {
        this(
                exhibitionId,
                title,
                posterUrl,
                genre,
                venueName,
                toStartDateTime(startDate),
                toEndDateTime(endDate),
                saleType,
                ageRating
        );
    }

    private static LocalDateTime toStartDateTime(LocalDate date) {
        return date == null ? null : date.atStartOfDay();
    }

    private static LocalDateTime toEndDateTime(LocalDate date) {
        return date == null ? null : date.atTime(LocalTime.of(23, 59, 59));
    }
}

package com.interpark_clone.domain.catalog.dto.response;

import com.interpark_clone.domain.catalog.dto.OpeningType;
import com.interpark_clone.domain.concert.entity.ConcertSchedule;
import com.interpark_clone.domain.exhibition.entity.Exhibition;
import com.interpark_clone.global.enums.AgeRating;
import com.interpark_clone.global.enums.SaleType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record OpeningResponse(
        OpeningType type,
        Long contentId,
        Long scheduleId,
        String title,
        String posterUrl,
        String venueName,
        LocalDateTime openAt,
        LocalDate startDate,
        LocalDate endDate,
        SaleType saleType,
        AgeRating ageRating,
        String status
) {
    public static OpeningResponse fromConcertSchedule(ConcertSchedule schedule) {
        return new OpeningResponse(
                OpeningType.CONCERT,
                schedule.getConcert().getId(),
                schedule.getId(),
                schedule.getConcert().getTitle(),
                schedule.getConcert().getPosterUrl(),
                schedule.getVenue().getName(),
                schedule.getOpenAt(),
                schedule.getStartDate().toLocalDate(),
                schedule.getEndDate().toLocalDate(),
                schedule.getConcert().getSaleType(),
                schedule.getConcert().getAgeRating(),
                schedule.getStatus().name()
        );
    }

    public static OpeningResponse fromExhibition(Exhibition exhibition) {
        return new OpeningResponse(
                OpeningType.EXHIBITION,
                exhibition.getId(),
                null,
                exhibition.getTitle(),
                exhibition.getPosterUrl(),
                exhibition.getVenue().getName(),
                exhibition.getOpenAt(),
                exhibition.getStartDate(),
                exhibition.getEndDate(),
                exhibition.getSaleType(),
                exhibition.getAgeRating(),
                exhibition.getStatus().name()
        );
    }
}

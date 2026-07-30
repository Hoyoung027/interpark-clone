package com.interpark_clone.domain.catalog.dto.response;

import com.interpark_clone.global.enums.Genre;
import com.interpark_clone.domain.concert.entity.ConcertSchedule;
import com.interpark_clone.domain.exhibition.entity.Exhibition;
import com.interpark_clone.global.enums.AgeRating;
import com.interpark_clone.global.enums.SaleType;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record OpeningResponse(
        Genre type,
        Long contentId,
        Long scheduleId,
        String title,
        String posterUrl,
        String venueName,
        LocalDateTime openAt,
        LocalDateTime startDate,
        LocalDateTime endDate,
        SaleType saleType,
        AgeRating ageRating,
        String status
) {
    public static OpeningResponse fromConcertSchedule(ConcertSchedule schedule) {
        return new OpeningResponse(
                Genre.CONCERT,
                schedule.getConcert().getId(),
                schedule.getId(),
                schedule.getConcert().getTitle(),
                schedule.getConcert().getPosterUrl(),
                schedule.getVenue().getName(),
                schedule.getOpenAt(),
                schedule.getStartDate(),
                schedule.getEndDate(),
                schedule.getConcert().getSaleType(),
                schedule.getConcert().getAgeRating(),
                schedule.getStatus().name()
        );
    }

    public static OpeningResponse fromExhibition(Exhibition exhibition) {
        return new OpeningResponse(
                Genre.EXHIBITION,
                exhibition.getId(),
                null,
                exhibition.getTitle(),
                exhibition.getPosterUrl(),
                exhibition.getVenue().getName(),
                exhibition.getOpenAt(),
                exhibition.getStartDate().atStartOfDay(),
                exhibition.getEndDate().atTime(LocalTime.MAX),
                exhibition.getSaleType(),
                exhibition.getAgeRating(),
                exhibition.getStatus().name()
        );
    }
}

package com.interpark_clone.domain.concert.dto.response;

import com.interpark_clone.domain.concert.entity.ConcertGenre;
import com.interpark_clone.global.enums.AgeRating;
import com.interpark_clone.global.enums.SaleType;

import java.time.LocalDateTime;

public record ConcertResponse(
        Long concertId,
        String title,
        String posterUrl,
        ConcertGenre genre,
        String venueName,
        LocalDateTime startDate,
        LocalDateTime endDate,
        SaleType saleType,
        AgeRating ageRating
) {
}

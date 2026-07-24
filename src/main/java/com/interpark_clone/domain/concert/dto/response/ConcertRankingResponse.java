package com.interpark_clone.domain.concert.dto.response;

import com.interpark_clone.domain.concert.entity.ConcertGenre;
import com.interpark_clone.global.enums.AgeRating;
import com.interpark_clone.global.enums.SaleType;

import java.time.LocalDateTime;

public record ConcertRankingResponse(
        int rank,
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
    public static ConcertRankingResponse of(int rank, ConcertRankingItemResponse item) {
        return new ConcertRankingResponse(
                rank,
                item.concertId(),
                item.title(),
                item.posterUrl(),
                item.genre(),
                item.venueName(),
                item.startDate(),
                item.endDate(),
                item.saleType(),
                item.ageRating()
        );
    }
}

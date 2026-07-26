package com.interpark_clone.domain.exhibition.dto.response;

import com.interpark_clone.domain.exhibition.entity.ExhibitionGenre;
import com.interpark_clone.global.enums.AgeRating;
import com.interpark_clone.global.enums.SaleType;

import java.time.LocalDateTime;

public record ExhibitionRankingResponse(
        int rank,
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
    public static ExhibitionRankingResponse of(int rank, ExhibitionRankingItemResponse item) {
        return new ExhibitionRankingResponse(
                rank,
                item.exhibitionId(),
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

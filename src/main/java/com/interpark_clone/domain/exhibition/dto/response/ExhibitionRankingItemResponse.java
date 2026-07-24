package com.interpark_clone.domain.exhibition.dto.response;

import com.interpark_clone.domain.exhibition.entity.ExhibitionGenre;
import com.interpark_clone.global.enums.AgeRating;
import com.interpark_clone.global.enums.SaleType;

import java.time.LocalDate;

public record ExhibitionRankingItemResponse(
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
}

package com.interpark_clone.domain.exhibition.dto.response;

import com.interpark_clone.domain.exhibition.entity.Exhibition;
import com.interpark_clone.domain.exhibition.entity.ExhibitionGenre;
import com.interpark_clone.domain.exhibition.entity.ExhibitionStatus;
import com.interpark_clone.domain.venue.entity.City;
import com.interpark_clone.domain.venue.entity.Venue;
import com.interpark_clone.global.enums.AgeRating;
import com.interpark_clone.global.enums.SaleType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExhibitionDetailResponse(
        Long exhibitionId,
        String title,
        String posterUrl,
        String description,
        ExhibitionGenre genre,
        ExhibitionStatus status,
        SaleType saleType,
        AgeRating ageRating,
        LocalDateTime openAt,
        LocalDate startDate,
        LocalDate endDate,
        VenueResponse venue
) {
    public static ExhibitionDetailResponse from(Exhibition exhibition) {
        return new ExhibitionDetailResponse(
                exhibition.getId(),
                exhibition.getTitle(),
                exhibition.getPosterUrl(),
                exhibition.getDescription(),
                exhibition.getGenre(),
                exhibition.getStatus(),
                exhibition.getSaleType(),
                exhibition.getAgeRating(),
                exhibition.getOpenAt(),
                exhibition.getStartDate(),
                exhibition.getEndDate(),
                VenueResponse.from(exhibition.getVenue())
        );
    }

    public record VenueResponse(
            Long venueId,
            String name,
            City city,
            String address
    ) {
        private static VenueResponse from(Venue venue) {
            return new VenueResponse(
                    venue.getId(),
                    venue.getName(),
                    venue.getCity(),
                    venue.getAddress()
            );
        }
    }
}

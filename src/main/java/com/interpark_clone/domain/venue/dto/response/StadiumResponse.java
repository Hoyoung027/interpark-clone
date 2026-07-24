package com.interpark_clone.domain.venue.dto.response;

import com.interpark_clone.domain.venue.entity.City;
import com.interpark_clone.domain.venue.entity.Venue;

public record StadiumResponse(
        Long venueId,
        String name,
        City city,
        String address,
        String imageUrl
) {
    public static StadiumResponse from(Venue venue) {
        return new StadiumResponse(
                venue.getId(),
                venue.getName(),
                venue.getCity(),
                venue.getAddress(),
                venue.getImageUrl()
        );
    }
}

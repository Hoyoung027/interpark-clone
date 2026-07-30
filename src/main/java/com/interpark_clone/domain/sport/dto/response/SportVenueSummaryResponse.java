package com.interpark_clone.domain.sport.dto.response;

import com.interpark_clone.domain.venue.entity.Venue;

public record SportVenueSummaryResponse(
        Long venueId,
        String name
) {
    public static SportVenueSummaryResponse from(Venue venue) {
        return new SportVenueSummaryResponse(
                venue.getId(),
                venue.getName()
        );
    }
}

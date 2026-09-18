package com.interpark_clone.domain.exhibition.dto.response;

public record ExhibitionAvailabilityResponse(
        Long exhibitionId,
        Integer remainingQuantity
) {
}

package com.interpark_clone.domain.sport.dto.response;

import com.interpark_clone.domain.sport.entity.BaseballClub;

public record SportClubResponse(
        Long clubId,
        String name,
        String imageUrl
) {
    public static SportClubResponse from(BaseballClub club) {
        return new SportClubResponse(
                club.getId(),
                club.getName(),
                club.getImageUrl()
        );
    }
}

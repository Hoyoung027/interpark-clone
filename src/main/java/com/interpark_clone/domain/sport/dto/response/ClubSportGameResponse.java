package com.interpark_clone.domain.sport.dto.response;

import com.interpark_clone.domain.sport.entity.BaseballGame;

import java.time.LocalDateTime;

public record ClubSportGameResponse(
        Long gameId,
        String venueName,
        LocalDateTime gameDate,
        Boolean preSaleAvailable,
        SportClubResponse homeClub,
        SportClubResponse awayClub
) {
    public static ClubSportGameResponse from(BaseballGame game) {
        return new ClubSportGameResponse(
                game.getId(),
                game.getVenue().getName(),
                game.getGameDate(),
                game.getPreSaleAvailable(),
                SportClubResponse.from(game.getHomeClub()),
                SportClubResponse.from(game.getAwayClub())
        );
    }
}

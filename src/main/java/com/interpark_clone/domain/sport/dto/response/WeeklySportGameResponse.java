package com.interpark_clone.domain.sport.dto.response;

import com.interpark_clone.domain.sport.entity.BaseballGame;

import java.time.LocalDateTime;

public record WeeklySportGameResponse(
        Long gameId,
        LocalDateTime gameDate,
        SportClubResponse homeClub,
        SportClubResponse awayClub,
        SportVenueSummaryResponse venue
) {
    public static WeeklySportGameResponse from(BaseballGame game) {
        return new WeeklySportGameResponse(
                game.getId(),
                game.getGameDate(),
                SportClubResponse.from(game.getHomeClub()),
                SportClubResponse.from(game.getAwayClub()),
                SportVenueSummaryResponse.from(game.getVenue())
        );
    }
}

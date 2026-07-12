package com.interpark_clone.domain.sport.football.entity;

import com.interpark_clone.domain.sport.common.entity.GameStatus;
import com.interpark_clone.domain.venue.entity.Venue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "football_game")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FootballGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "football_game_id")
    private Long id;

    @Column(name = "game_date", nullable = false)
    private LocalDateTime gameDate;

    @Column(name = "open_at", nullable = false)
    private LocalDateTime openAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GameStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_club_id", nullable = false)
    private FootballClub homeClub;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_club_id", nullable = false)
    private FootballClub awayClub;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Builder
    private FootballGame(
            LocalDateTime gameDate,
            LocalDateTime openAt,
            GameStatus status,
            FootballClub homeClub,
            FootballClub awayClub,
            Venue venue
    ) {
        this.gameDate = gameDate;
        this.openAt = openAt;
        this.status = status;
        this.homeClub = homeClub;
        this.awayClub = awayClub;
        this.venue = venue;
    }
}

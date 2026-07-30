package com.interpark_clone.domain.sport.entity;

import com.interpark_clone.domain.venue.entity.Venue;
import com.interpark_clone.global.entity.BaseEntity;
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
@Table(name = "baseball_game")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseballGame extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "baseball_game_id")
    private Long id;

    @Column(name = "game_date", nullable = false)
    private LocalDateTime gameDate;

    @Column(name = "open_at", nullable = false)
    private LocalDateTime openAt;

    @Column(name = "pre_sale_available", nullable = false)
    private Boolean preSaleAvailable = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GameStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_club_id", nullable = false)
    private BaseballClub homeClub;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_club_id", nullable = false)
    private BaseballClub awayClub;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Builder
    private BaseballGame(
            LocalDateTime gameDate,
            LocalDateTime openAt,
            Boolean preSaleAvailable,
            GameStatus status,
            BaseballClub homeClub,
            BaseballClub awayClub,
            Venue venue
    ) {
        this.gameDate = gameDate;
        this.openAt = openAt;
        this.preSaleAvailable = preSaleAvailable != null && preSaleAvailable;
        this.status = status;
        this.homeClub = homeClub;
        this.awayClub = awayClub;
        this.venue = venue;
    }
}

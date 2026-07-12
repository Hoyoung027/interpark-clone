package com.interpark_clone.domain.sport.football.entity;

import com.interpark_clone.domain.sport.common.entity.ClubHomeCity;
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

@Entity
@Table(name = "football_club")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FootballClub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "football_club_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClubHomeCity home;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Builder
    private FootballClub(String name, ClubHomeCity home, String imageUrl, Venue venue) {
        this.name = name;
        this.home = home;
        this.imageUrl = imageUrl;
        this.venue = venue;
    }
}

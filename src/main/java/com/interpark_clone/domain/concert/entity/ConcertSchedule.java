package com.interpark_clone.domain.concert.entity;

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
@Table(name = "concert_schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConcertSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_schedule_id")
    private Long id;

    @Column(name = "perform_date", nullable = false)
    private LocalDateTime performDate;

    @Column(name = "open_at", nullable = false)
    private LocalDateTime openAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConcertStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @Builder
    private ConcertSchedule(
            LocalDateTime performDate,
            LocalDateTime openAt,
            ConcertStatus status,
            Venue venue,
            Concert concert
    ) {
        this.performDate = performDate;
        this.openAt = openAt;
        this.status = status;
        this.venue = venue;
        this.concert = concert;
    }
}

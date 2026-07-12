package com.interpark_clone.domain.exhibition.entity;

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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "exhibition")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Exhibition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exhibition_id")
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "open_at", nullable = false)
    private LocalDateTime openAt;

    @Column(nullable = false)
    private Boolean earlybird;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExhibitionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Builder
    private Exhibition(
            String title,
            LocalDate startDate,
            LocalDate endDate,
            LocalDateTime openAt,
            Boolean earlybird,
            ExhibitionStatus status,
            Venue venue
    ) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.openAt = openAt;
        this.earlybird = earlybird;
        this.status = status;
        this.venue = venue;
    }
}

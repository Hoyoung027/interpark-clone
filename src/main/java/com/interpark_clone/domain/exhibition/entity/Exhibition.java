package com.interpark_clone.domain.exhibition.entity;

import com.interpark_clone.domain.venue.entity.Venue;
import com.interpark_clone.global.code.BusinessErrorCode;
import com.interpark_clone.global.entity.BaseEntity;
import com.interpark_clone.global.enums.AgeRating;
import com.interpark_clone.global.enums.SaleType;
import com.interpark_clone.global.exception.BusinessException;
import jakarta.persistence.*;
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
public class Exhibition extends BaseEntity {

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

    @Column(name = "poster_url", length = 500)
    private String posterUrl;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ExhibitionGenre genre;

    @Lob
    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SaleType saleType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AgeRating ageRating;

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
            String posterUrl,
            ExhibitionGenre genre,
            String description,
            SaleType saleType,
            AgeRating ageRating,
            ExhibitionStatus status,
            Venue venue
    ) {

        if (startDate == null || endDate == null || !startDate.isBefore(endDate)) {
            throw new BusinessException(BusinessErrorCode.INVALID_EXHIBITION_SCHEDULE);
        }

        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.openAt = openAt;
        this.posterUrl = posterUrl;
        this.genre = genre;
        this.description = description;
        this.saleType = saleType;
        this.ageRating = ageRating;
        this.status = status;
        this.venue = venue;
    }

    public void increaseViewCount() {
        this.viewCount = this.viewCount == null ? 1 : this.viewCount + 1;
    }
}

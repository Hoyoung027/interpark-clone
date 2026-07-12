package com.interpark_clone.domain.concert.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "concert")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ConcertGenre genre;

    @Column(name = "poster_url", length = 500)
    private String posterUrl;

    @Lob
    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConcertStatus status;

    @Builder
    private Concert(String title, ConcertGenre genre, String posterUrl, String description, ConcertStatus status) {
        this.title = title;
        this.genre = genre;
        this.posterUrl = posterUrl;
        this.description = description;
        this.status = status;
    }
}

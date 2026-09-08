package com.interpark_clone.domain.venue.entity;

import com.interpark_clone.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "section")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SectionGrade grade;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "seat_count")
    private Integer seatCount;

    @Builder
    public Section(Venue venue, String name, SectionGrade grade, Integer price, Integer seatCount) {
        this.venue = venue;
        this.name = name;
        this.grade = grade;
        this.price = price;
        this.seatCount = seatCount;
    }
}

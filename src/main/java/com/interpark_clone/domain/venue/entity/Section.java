package com.interpark_clone.domain.venue.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "section")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @Builder
    public Section(Venue venue, String name, SectionGrade grade, Integer price) {
        this.venue = venue;
        this.name = name;
        this.grade = grade;
        this.price = price;
    }
}

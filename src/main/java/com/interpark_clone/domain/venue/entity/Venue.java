package com.interpark_clone.domain.venue.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "venue")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "venue_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private City city;

    @Column(nullable = false, length = 255)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VenueType type;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Builder
    private Venue(String name, City city, String address, VenueType type, String imageUrl) {
        this.name = name;
        this.city = city;
        this.address = address;
        this.type = type;
        this.imageUrl = imageUrl;
    }
}

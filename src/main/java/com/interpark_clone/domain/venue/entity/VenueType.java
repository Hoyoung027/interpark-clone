package com.interpark_clone.domain.venue.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VenueType {
    CONCERT_HALL("공연장"),
    STADIUM("경기장"),
    EXHIBITION_HALL("전시장");

    private final String description;
}
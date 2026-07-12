package com.interpark_clone.domain.venue.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SectionGrade {

    REGULAR("일반 구역"),
    SPECIAL("특별석"),
    VIP("VIP석");

    private final String description;
}

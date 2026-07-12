package com.interpark_clone.domain.reservation.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {
    CONCERT("콘서트"),
    BASEBALL("야구"),
    FOOTBALL("축구"),
    EXHIBITION("전시");

    private final String description;
}

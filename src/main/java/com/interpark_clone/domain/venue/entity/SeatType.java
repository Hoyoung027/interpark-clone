package com.interpark_clone.domain.venue.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SeatType {
    NORMAL("일반석"),
    WHEELCHAIR("장애인석"),
    STANDING("스탠딩");

    private final String description;
}

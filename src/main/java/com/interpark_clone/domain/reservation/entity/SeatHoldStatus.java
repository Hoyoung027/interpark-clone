package com.interpark_clone.domain.reservation.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SeatHoldStatus {
    HOLDING("선점 중"),
    CONFIRMED("확정"),
    EXPIRED("만료"),
    RELEASED("해제");

    private final String description;
}

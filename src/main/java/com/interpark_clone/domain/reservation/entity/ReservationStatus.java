package com.interpark_clone.domain.reservation.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
    PENDING("예매 대기"),
    CONFIRMED("예매 확정"),
    CANCELED("예매 취소"),
    EXPIRED("예매 만료");

    private final String description;
}

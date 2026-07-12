package com.interpark_clone.domain.reservation.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QueueStatus {

    WAITING("대기 중"),
    ENTERED("입장 허용"),
    EXPIRED("순번 소멸");

    private final String description;
}

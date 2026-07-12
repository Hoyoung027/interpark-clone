package com.interpark_clone.domain.concert.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConcertStatus {
    UPCOMING("공연 예정"),
    OPEN("예매 가능"),
    SOLD_OUT("매진"),
    CLOSED("예매 종료"),
    CANCELED("취소"),
    ENDED("공연 종료");

    private final String description;
}

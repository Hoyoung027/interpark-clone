package com.interpark_clone.domain.sport.common.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameStatus {
    SCHEDULED("예정"),
    OPEN("예매 가능"),
    SOLD_OUT("매진"),
    CLOSED("예매 종료"),
    CANCELED("취소"),
    FINISHED("종료");

    private final String description;
}

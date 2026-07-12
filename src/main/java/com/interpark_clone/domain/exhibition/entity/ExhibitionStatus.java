package com.interpark_clone.domain.exhibition.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExhibitionStatus {
    UPCOMING("전시 예정"),
    OPEN("예매 가능"),
    CLOSED("예매 종료"),
    CANCELED("취소"),
    ENDED("전시 종료");

    private final String description;
}

package com.interpark_clone.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgeRating {
    ALL("전체관람가"),
    AGE_7("7세 이상"),
    AGE_12("12세 이상"),
    AGE_15("15세 이상"),
    AGE_19("19세 이상");

    private final String description;
}

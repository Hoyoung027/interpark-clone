package com.interpark_clone.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortType {
    VIEW("조회수순"),
    RESERVATION("예매순"),
    CLOSING_SOON("종료일 임박순"),
    LATEST("최근 등록순"),
    OPEN_AT("오픈일 임박순");

    private final String description;
}

package com.interpark_clone.domain.exhibition.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExhibitionGenre {
    EXHIBITION("전시회"),
    MUSEUM("뮤지엄"),
    EVENT_FESTIVAL("행사/축제"),
    CHILD_EXPERIENCE("아동체험전");

    private final String description;
}

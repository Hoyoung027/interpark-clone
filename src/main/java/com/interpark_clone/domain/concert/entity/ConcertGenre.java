package com.interpark_clone.domain.concert.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConcertGenre {
    BALLAD("발라드"),
    ROCK_METAL("락/메탈"),
    RAP_HIPHOP("랩/힙합"),
    JAZZ_SOUL("재즈/소울"),
    DINNER_SHOW("디너쇼"),
    FOLK_TROT("포크/트로트"),
    OVERSEAS("내한공연"),
    FESTIVAL("페스티벌"),
    FAN_MEETING("팬클럽/팬미팅");

    private final String description;
}

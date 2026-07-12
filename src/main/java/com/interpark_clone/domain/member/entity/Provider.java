package com.interpark_clone.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {
    LOCAL("일반"),
    KAKAO("카카오"),
    NAVER("네이버"),
    GOOGLE("구글");

    private final String description;
}



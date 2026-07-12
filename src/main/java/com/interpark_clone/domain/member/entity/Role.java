package com.interpark_clone.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ROLE_ADMIN("관리자"),
    ROLE_USER("일반 회원");

    private final String description;
}

package com.interpark_clone.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessErrorCode implements Code{

    // 멤버 관련 에러
    MEMBER_NOT_FOUND(404, "존재하지 않는 회원입니다.");

    private final int statusCode;
    private final String message;

}

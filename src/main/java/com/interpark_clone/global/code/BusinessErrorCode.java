package com.interpark_clone.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessErrorCode implements Code{

    // 멤버 관련 에러
    MEMBER_NOT_FOUND(404, "존재하지 않는 회원입니다."),
    MEMBER_ALREADY_EXISTS(409, "이미 가입된 회원입니다."),
    LOGIN_FAILED(401, "이메일 혹은 비밀번호가 올바르지 않습니다."),

    // 조회 기간 관련 에러
    DATE_RANGE_TOO_LARGE(400, "조회 가능한 최대 기간을 초과했습니다."),

    // 콘서트 관련 에러
    CONCERT_NOT_FOUND(404, "존재하지 않는 콘서트입니다."),
    INVALID_CONCERT_SCHEDULE(400, "유효하지 않은 콘서트 일정입니다."),

    // 전시 관련 에러
    EXHIBITION_NOT_FOUND(404, "존재하지 않는 전시입니다."),
    INVALID_EXHIBITION_SCHEDULE(400, "유효하지 않은 전시 일정입니다."),

    // 경기장 관련 에러
    VENUE_NOT_FOUND(404, "존재하지 않는 경기장입니다."),

    // 예약 관련 에러
    INVALID_RESERVATION_QUANTITY(400, "예약 수량은 1 이상이어야 합니다.");

    private final int statusCode;
    private final String message;

}

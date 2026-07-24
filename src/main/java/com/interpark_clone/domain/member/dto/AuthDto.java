package com.interpark_clone.domain.member.dto;

import com.interpark_clone.domain.member.dto.response.AuthResponse;
import org.springframework.http.ResponseCookie;

public record AuthDto(
        AuthResponse authResponse,
        ResponseCookie accessTokenCookie,
        ResponseCookie refreshTokenCookie
) {
}
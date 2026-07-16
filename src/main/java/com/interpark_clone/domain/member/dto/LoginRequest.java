package com.interpark_clone.domain.member.dto;

public record LoginRequest(
        String email,
        String password
) {
}

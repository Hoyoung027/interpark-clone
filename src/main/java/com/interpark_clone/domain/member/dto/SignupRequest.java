package com.interpark_clone.domain.member.dto;

public record SignupRequest(
        String email,
        String password
) {
}

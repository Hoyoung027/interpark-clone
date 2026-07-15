package com.interpark_clone.domain.member.controller;

import com.interpark_clone.domain.member.service.AuthService;
import com.interpark_clone.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/login")
    public ResponseEntity<Response<?>> login() {
        return null;
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<Response<?>>  signup() {
        return null;
    }
}

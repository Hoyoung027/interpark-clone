package com.interpark_clone.domain.member.controller;

import com.interpark_clone.domain.member.dto.AuthDto;
import com.interpark_clone.domain.member.dto.LoginRequest;
import com.interpark_clone.domain.member.dto.LogoutDto;
import com.interpark_clone.domain.member.dto.SignupRequest;
import com.interpark_clone.domain.member.dto.AuthResponse;
import com.interpark_clone.domain.member.service.AuthService;
import com.interpark_clone.global.code.SuccessCode;
import com.interpark_clone.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/login")
    public ResponseEntity<Response<AuthResponse>> login(@RequestBody LoginRequest request) {
        AuthDto loginResponse = authService.localLogin(request);
        Response<AuthResponse> response = Response.success(
                SuccessCode.LOGIN_SUCCESS,
                loginResponse.authResponse(),
                "로그인 API"
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, loginResponse.accessTokenCookie().toString())
                .header(HttpHeaders.SET_COOKIE, loginResponse.refreshTokenCookie().toString())
                .body(response);
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<Response<AuthResponse>> signup(@RequestBody SignupRequest request) {
        AuthDto signupResponse  = authService.signup(request);
        Response<AuthResponse> response = Response.success(
                SuccessCode.INSERT_SUCCESS,
                signupResponse.authResponse(),
                "회원 가입 API"
        );
        return ResponseEntity.status(201)
                .header(HttpHeaders.SET_COOKIE, signupResponse.accessTokenCookie().toString())
                .header(HttpHeaders.SET_COOKIE, signupResponse.refreshTokenCookie().toString())
                .body(response);
    }

    @PostMapping("/auth/reissue")
    public ResponseEntity<Response<AuthResponse>> reissue(
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        AuthDto reissueResponse = authService.reissue(refreshToken);
        Response<AuthResponse> response = Response.success(
                SuccessCode.LOGIN_SUCCESS,
                reissueResponse.authResponse(),
                "토큰 재발급 API"
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, reissueResponse.accessTokenCookie().toString())
                .header(HttpHeaders.SET_COOKIE, reissueResponse.refreshTokenCookie().toString())
                .body(response);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Response<Void>> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        LogoutDto logoutResponse = authService.logout(refreshToken);
        Response<Void> response = Response.success(SuccessCode.DELETE_SUCCESS);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, logoutResponse.accessTokenCookie().toString())
                .header(HttpHeaders.SET_COOKIE, logoutResponse.refreshTokenCookie().toString())
                .body(response);
    }
}

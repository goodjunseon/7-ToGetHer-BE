package com.together.backend.auth;

import com.together.backend.global.jwt.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final JwtTokenService jwtTokenService;
    private final AuthService authService;

    @PostMapping("/token/refresh")
    public void refreshAccessToken() {
        // Refresh Token을 사용하여 Access Token을 재발급하는 로직
    }

    @PostMapping("/logout")
    public void logout() {
        // 사용자가 로그아웃하는 로직
        // AccessToken은 블랙리스트 추가, RefreshToken은 삭제
    }

}

package com.together.backend.auth;

import com.together.backend.global.jwt.service.BlackListTokenService;
import com.together.backend.global.jwt.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final BlackListTokenService blackListTokenService;
    private final JwtTokenService jwtTokenService;

    public void logout(String accessToken) {
        // 블랙리스트에 토큰 추가
        blackListTokenService.blackListTokenSave(accessToken);

        // 리프레시 토큰 삭제
        jwtTokenService.refreshTokenDelete(accessToken);
    }
}

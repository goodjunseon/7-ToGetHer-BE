package com.together.backend.user.service;

import com.together.backend.global.security.jwt.service.BlackListTokenService;
import com.together.backend.global.security.jwt.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final BlackListTokenService blackListTokenService;
    private final JwtTokenService jwtTokenService;

    public void logout(String accessToken) {
        // 블랙리스트에 토큰 추가
        blackListTokenService.blackListTokenSave(accessToken);

        // 리프레시 토큰 삭제
        jwtTokenService.refreshTokenDelete(accessToken);
    }

}

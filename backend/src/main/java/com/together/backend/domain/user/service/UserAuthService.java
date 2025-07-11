package com.together.backend.domain.user.service;

import com.together.backend.global.security.jwt.service.BlackListTokenService;
import com.together.backend.global.security.jwt.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAuthService {

    private final BlackListTokenService blackListTokenService;
    private final JwtTokenService jwtTokenService;

    public void logout(String accessToken) {
        // 블랙리스트에 토큰 추가
        blackListTokenService.blackListTokenSave(accessToken);
        log.info("blackListTokenService.blackListTokenSave() 호출");

        // 리프레시 토큰 삭제
        jwtTokenService.refreshTokenDelete(accessToken);
        log.info("jwtTokenService.refreshTokenDelete() 호출");
    }

}

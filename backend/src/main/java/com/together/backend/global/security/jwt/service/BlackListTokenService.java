package com.together.backend.global.security.jwt.service;

import com.together.backend.global.security.jwt.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BlackListTokenService {
    private final JWTUtil jwtUtil;
    @Qualifier("redisTemplate")
    private final RedisTemplate<String, String> redisTemplate;


    public void blackListTokenSave(String accessToken) {
        // 1. Access Token에서 남은 만료 시간 계산
        Long expiration = jwtUtil.getExpiration(accessToken);

        // 2. Redis에 저장: key = "blacklist:{토큰}", value = "logout"
        redisTemplate.opsForValue().set("blacklist: " + accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
    }

    public boolean isBlackListed(String accessToken) {
        // 1. Redis에서 블랙리스트 토큰 조회
        String value = redisTemplate.opsForValue().get("blacklist: " + accessToken);

        // 2. 블랙리스트에 존재하면 true 반환
        return value != null && value.equals("logout");
    }

}

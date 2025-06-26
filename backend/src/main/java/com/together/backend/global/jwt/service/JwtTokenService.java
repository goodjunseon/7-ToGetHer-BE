package com.together.backend.global.jwt.service;

import com.together.backend.global.jwt.JWTUtil;
import com.together.backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;


@Service
public class JwtTokenService {
    private final JWTUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;

    public JwtTokenService(
            JWTUtil jwtUtil,
            UserRepository userRepository,
            @Qualifier("redisTemplate") RedisTemplate<String, String> redisTemplate
    ) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    // Refresh Token을 Redis에 저장하는 메서드
    public void refreshTokenSave(String socialId, String refreshToken) {
        try {
            System.out.println(">>> 저장시도: " + socialId + " / " + refreshToken);
            System.out.println(">>> RedisTemplate: " + redisTemplate);
            Long expiration = jwtUtil.getExpiration(refreshToken); // 만료 시간

            System.out.println("refreshToken Redis 저장 시도: " + socialId + " / " + refreshToken + " / 만료시간: " + expiration);
            redisTemplate.opsForValue().set("refreshToken:" + socialId,
                    refreshToken,
                    expiration,
                    TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            System.out.println("Redis 저장 실패: " + e.getMessage());
        }
    }

    // Refresh Token 제거
    @Transactional
    public void refreshTokenDelete(String refreshToken) {
        // 1. 토큰에서 socialId 추출
        String socialId = jwtUtil.getUsername(refreshToken); // socialId 추출
        if (socialId != null) {
            redisTemplate.delete("refreshToken:" + socialId);
        }
    }

    // Access 토큰 재발급
    public String reissueAccessToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        String socialId = jwtUtil.getUsername(refreshToken);
        String stored = redisTemplate.opsForValue().get("refreshToken:" + socialId);

        if (stored == null || !stored.equals(refreshToken)) {
            throw new RuntimeException("Refresh Token 불일치");
        }

        // 3. 새로운 Access Token 생성
        String role = jwtUtil.getRole(refreshToken);
        return jwtUtil.createToken(socialId, role);
    }

}

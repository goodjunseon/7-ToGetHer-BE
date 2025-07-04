package com.together.backend.user.controller;


import com.together.backend.global.security.jwt.service.JwtTokenService;
import com.together.backend.global.security.jwt.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final JwtTokenService jwtTokenService;

    // 검증 완료
    @PostMapping("/token/refresh")
    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {

        String accessToken = CookieUtil.getCookieValue(request, "accessToken");
        String refreshToken = CookieUtil.getCookieValue(request, "refreshToken");
        // 새로운 AccessToken 발급
        String newAccessToken = jwtTokenService.reissueAccessToken(accessToken, refreshToken);

        if (newAccessToken != null) {
            // 새로운 AccessToken을 쿠키에 저장
            CookieUtil.createCookie(response, "accessToken", newAccessToken);
        } else {
            throw new RuntimeException("새로운 AccessToken을 발급할 수 없습니다.");
        }
    }
}

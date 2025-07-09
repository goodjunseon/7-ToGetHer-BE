package com.together.backend.domain.user.controller;


import com.together.backend.domain.user.model.entity.User;
import com.together.backend.domain.user.repository.UserRepository;
import com.together.backend.global.common.BaseResponse;
import com.together.backend.global.common.BaseResponseStatus;
import com.together.backend.global.security.jwt.service.JwtTokenService;
import com.together.backend.global.security.jwt.util.CookieUtil;
import com.together.backend.global.security.jwt.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final JwtTokenService jwtTokenService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    // 검증 완료
    @PostMapping("/token/refresh")
    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = CookieUtil.getCookieValue(request, "refreshToken");
        // 새로운 AccessToken 발급
        String newAccessToken = jwtTokenService.reissueAccessToken(refreshToken);

        if (newAccessToken != null) {
            // 새로운 AccessToken을 쿠키에 저장
            CookieUtil.createCookie(response, "accessToken", newAccessToken);
        } else {
            throw new RuntimeException("새로운 AccessToken을 발급할 수 없습니다.");
        }
    }

    @GetMapping("/test")
    public BaseResponse<String> issueTestToken(@RequestParam("email") String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        String token = jwtUtil.createToken(user.getEmail(), user.getUserId(), user.getRole().name());
        return new BaseResponse<>(BaseResponseStatus.OK, token);
    }
}

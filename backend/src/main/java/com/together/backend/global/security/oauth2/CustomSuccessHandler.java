package com.together.backend.global.security.oauth2;

import com.together.backend.global.security.jwt.JWTUtil;
import com.together.backend.global.security.jwt.service.JwtTokenService;
import com.together.backend.global.security.oauth2.dto.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final JwtTokenService jwtTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("CustomSuccessHandler 실행됨");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Referer: " + request.getHeader("referer"));

        // OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String socialId = customUserDetails.getSocialId();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = "ROLE_USER"; // 기본값으로 ROLE_USER 설정

        String token = jwtUtil.createToken(socialId, role);
        System.out.println("Generated JWT Token: " + token);  // ✅ 토큰 생성 확인

        Cookie accessCookie = createCookie("accessToken", token);
        System.out.println("Setting Cookie: " + accessCookie.getName() + "=" + accessCookie.getValue()); // ✅ 쿠키 확인

        String refreshToken = jwtUtil.createRefreshToken(socialId);
        jwtTokenService.refreshTokenSave(socialId, refreshToken);


        // 개발 환경 테스트용
        response.addCookie(accessCookie);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("{\"message\": \"OAuth2 login success. JWT cookie set.\"}");
        response.flushBuffer();  // 확실하게 커밋

        // 프론트와 연결 후 리디렉션 설정
//        response.addCookie(createCookie("accessToken", token));
//        response.sendRedirect("http://localhost:3000/");
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/"); // 모든 경로에서 접근 가능
        cookie.setMaxAge(60*60*12); // 12시간
        cookie.setSecure(false); // 배포시 true로 변경하여 HTTPS에서만 전송되도록 설정
        cookie.setHttpOnly(true); // JavaScript에서 접근 불가

        return cookie;
    }
}

package com.together.backend.global.security.oauth2;

import com.together.backend.domain.user.repository.UserRepository;
import com.together.backend.global.security.jwt.util.CookieUtil;
import com.together.backend.global.security.jwt.util.JWTUtil;
import com.together.backend.global.security.jwt.service.JwtTokenService;
import com.together.backend.global.security.oauth2.dto.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;


@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final JwtTokenService jwtTokenService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("CustomSuccessHandler: onAuthenticationSuccess 호출됨");

        // OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String email = customUserDetails.getEmail();
        Long userId = customUserDetails.getUserId();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = "ROLE_USER"; // 기본값으로 ROLE_USER 설정


        String accessToken = jwtUtil.createToken(email,userId, role);
        String refreshToken = jwtUtil.createRefreshToken(email);

        CookieUtil.createCookie(response,"accessToken", accessToken);
        CookieUtil.createCookie(response, "refreshToken", refreshToken);

        jwtTokenService.refreshTokenSave(email, refreshToken);

        response.sendRedirect("http://7-together.kro.kr/?step=2");

//        response.getWriter().write("{\"message\": \"OAuth2 login success. JWT cookie set.\"}");
//        response.flushBuffer();  // 확실하게 커밋

        // 프론트와 연결 후 리디렉션 설정
//        response.addCookie(createCookie("accessToken", token));
//        response.addCookie(createCookie("refreshToken", refreshToken));


        //        // 개발 환경 테스트용
//        response.setStatus(HttpServletResponse.SC_OK);
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
    }
}

package com.together.backend.global.security.jwt;


import com.together.backend.global.security.jwt.service.BlackListTokenService;
import com.together.backend.global.security.jwt.util.CookieUtil;
import com.together.backend.global.security.jwt.util.JWTUtil;
import com.together.backend.global.security.oauth2.dto.CustomOAuth2User;
import com.together.backend.global.security.oauth2.dto.UserDTO;
import com.together.backend.domain.user.model.entity.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final BlackListTokenService blackListTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        System.out.println("JWTFilter: " + uri);

        if (uri.startsWith("/api/user/login/kakao") || uri.startsWith("/oauth2/authorization/kakao")) {
            // 카카오 로그인 관련 요청은 JWT 검증을 하지 않음
            System.out.println("JWTFilter: 카카오 로그인 관련 요청이므로 다음 필터로 이동");
            filterChain.doFilter(request, response);
            return;
        }

        // 쿠키에서 accessToken 꺼내기
        String token = CookieUtil.getCookieValue(request, "accessToken");

        //Authorization 헤더 검증
        if (token == null) {
            System.out.println("JWTFilter: token null이므로 다음 필터로 이동");
            filterChain.doFilter(request, response);
            // 토큰 없으면 JWT 검증을 하지 않고 다음 필터로 넘어감
            return;
        }

        // 블랙리스트 검증
        if (blackListTokenService.isBlackListed(token)) {
            log.info("JWTFilter: 블랙리스트에 등록된 토큰입니다.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"블랙리스트에 등록된 토큰입니다. 다시 로그인 해주세요.\"}");
            return;
        }

        //토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {
            System.out.println("token expired");
            filterChain.doFilter(request, response);
            //조건이 해당되면 메소드 종료
            return;
        }

        // 토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        //userDTO를 생성하여 값 set
        UserDTO userDTO = UserDTO.builder()
                .email(username)
                .role(Role.valueOf(role))
                .build();

        // UserDetails에 회원 정보 객체 담기
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

        // 스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());

        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}

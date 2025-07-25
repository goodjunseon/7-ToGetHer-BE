package com.together.backend.global.config;

import com.together.backend.global.security.jwt.JWTFilter;
import com.together.backend.global.security.jwt.service.BlackListTokenService;
import com.together.backend.global.security.jwt.util.CookieUtil;
import com.together.backend.global.security.jwt.util.JWTUtil;
import com.together.backend.global.security.oauth2.CustomOAuth2UserService;
import com.together.backend.global.security.oauth2.CustomSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final BlackListTokenService blackListTokenService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("https://7-together.kro.kr");
        config.addAllowedOrigin("http://localhost:3000");

        config.addAllowedHeader("Origin");
        config.addAllowedHeader("X-Requested-With");
        config.addAllowedHeader("Content-Type");
        config.addAllowedHeader("Accept");
        config.addAllowedHeader("Authorization");  // 필요시
        config.addAllowedMethod("*");
        config.addExposedHeader("Set-Cookie");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // csrf disable
                .csrf(auth -> auth.disable())
                // form 로그인 방식 disable
                .formLogin((auth)-> auth.disable())
                //HTTP Basic 인증 disable
                .httpBasic((auth) -> auth.disable());

        //== jwt 설정 ==//
        http
                .addFilterBefore(new JWTFilter(jwtUtil, blackListTokenService), UsernamePasswordAuthenticationFilter.class);

        //== 소셜 로그인 설정 ==//
        http
                .oauth2Login(oauth2 -> oauth2
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                        .failureHandler((request, response, exception) -> {
                            log.error("OAuth2 로그인 실패", exception);
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "OAuth2 인증 실패: " + exception.getMessage());
                        }));

        // 경로별 인가 작업
        http
                .authorizeHttpRequests(auth-> auth
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                                .requestMatchers("/**").permitAll() // 개발 환경에서 모든 요청 허용
                        .requestMatchers ("/", "/oauth2/**", "/api/user/login/kakao","/api/auth/test/**").permitAll()
                        .anyRequest().authenticated() // 인증이 필요할 땐 자동으로 oauth2Login() 실행
                );

        // 세션 설정 : STATELESS
        http
                .sessionManagement((session) ->session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
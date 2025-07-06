package com.together.backend.global.security.jwt.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

@Slf4j
@Component
public class CookieUtil {

    public static String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return null;
        }

        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                log.info("CookieUtil: 쿠키 이름: {}, 값: {}", cookie.getName(), cookie.getValue());
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void createCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/"); // 모든 경로에서 접근 가능
        cookie.setMaxAge(60*60*12); // 12시간
        cookie.setSecure(false); // 배포시 true로 변경하여 HTTPS에서만 전송되도록 설정
        cookie.setHttpOnly(true); // JavaScript에서 접근 불가
        response.addCookie(cookie); // 쿠키를 응답에 추가
    }

    public static Cookie deleteCookie(String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0); // 쿠키 즉시 만료
        cookie.setPath("/"); // 쿠키 경로 설정
        cookie.setHttpOnly(true);
        return cookie; // 삭제할 쿠키 반환
    }
}

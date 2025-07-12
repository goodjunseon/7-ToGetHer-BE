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
            log.warn("@@@@@getCookieValue:null@@@@@");
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
        int maxAge = 60 * 60 * 12; // 12시간
        String cookieValue = String.format(
                "%s=%s; Path=/; Max-Age=%d; Secure; HttpOnly; SameSite=None; Domain=7-together.kro.kr",
                name, value, maxAge
        );
        response.addHeader("Set-Cookie", cookieValue); // addHeader로 여러 개 가능
    }

    public static void expireCookie(HttpServletResponse response, String name) {
        String expiredCookie = String.format(
                "%s=; Path=/; Max-Age=0; Secure; HttpOnly; SameSite=None; Domain=7-together.kro.kr",
                name
        );
        response.addHeader("Set-Cookie", expiredCookie);
    }
}

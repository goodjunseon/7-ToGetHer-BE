package com.together.backend.user.controller;

import com.together.backend.global.common.BaseResponse;
import com.together.backend.global.common.BaseResponseStatus;
import com.together.backend.global.security.jwt.util.CookieUtil;
import com.together.backend.global.security.oauth2.dto.CustomOAuth2User;
import com.together.backend.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    // 사용자 관련 API 정의
    // 예: 로그인, 사용자 정보 조회, 등
    private final UserService userService;


    @PostMapping("/logout")
    public BaseResponse<String> logout(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키에서 AccessTokne 꺼내기
        String accessToken = CookieUtil.getCookieValue(request, "accessToken");

        // AccessToken이 없으면 에러 응답
        if (accessToken == null) {
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED);
        }

        userService.logout(accessToken);

        // 로그아웃 성공 시 쿠키 삭제
        Cookie cookie = CookieUtil.deleteCookie("accessToken");
        response.addCookie(cookie);

        return new BaseResponse<>(BaseResponseStatus.OK);
    }


    @DeleteMapping("/")
    public BaseResponse<String> deleteUser(@AuthenticationPrincipal CustomOAuth2User oAuth2User) throws Exception {

        log.info("deleteUser() 호출됨");
        String email = oAuth2User.getEmail();
        System.out.println("소셜 아이디: " + email);
        userService.deleteUser(email);


        return new BaseResponse<>(BaseResponseStatus.OK);
    }


    @GetMapping("/")
    public String getUsers() {
        return "사용자 목록 조회 API";
    }

    @GetMapping("/{id}")
    public String getUserById() {
        return "사용자 정보 조회 API";
    }

    @PostMapping("/{id}")
    public String updateUser() {
        return "사용자 정보 수정 API";
    }

}
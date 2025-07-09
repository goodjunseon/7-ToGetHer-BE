package com.together.backend.domain.user.controller;

import com.together.backend.domain.user.model.entity.Role;
import com.together.backend.domain.user.model.request.UserRequest;
import com.together.backend.domain.user.model.response.MyPageResponse;
import com.together.backend.domain.user.model.response.UserResponse;
import com.together.backend.global.common.BaseResponse;
import com.together.backend.global.common.BaseResponseStatus;
import com.together.backend.global.security.jwt.util.CookieUtil;
import com.together.backend.global.security.oauth2.dto.CustomOAuth2User;
import com.together.backend.domain.user.service.UserService;
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

    /*
    * 쿠키 관련 로직은 HTTP 응답에 직접적으로 관여하는 작업이므로 서비스 계층이 아닌 컨트롤러 계층에서 처리하는 것이 적합
    * 서비스 계층은 비즈니스 로직만 담당, HTTP 관련 작업(쿠키,세션)은 컨트롤러 계층에서 처리하는 것이 적합하다.
     */

    @PostMapping("/logout")
    public BaseResponse<String> logout(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키에서 AccessTokne 꺼내기
        String accessToken = CookieUtil.getCookieValue(request, "accessToken");

        // AccessToken이 없으면 에러 응답
        if (accessToken == null) {
            log.warn("로그아웃 요청: accessToken 없음");
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED, "accessToken이 존재하지 않습니다.");
        }

        try {
            userService.logout(accessToken);
            Cookie cookie = CookieUtil.deleteCookie("accessToken", "/");
            response.addCookie(cookie);
            return new BaseResponse<>(BaseResponseStatus.OK);
        } catch (Exception e) {
            log.error("로그아웃 처리 중 오류 발생", e);
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/")
    public BaseResponse<String> deleteUser(@AuthenticationPrincipal CustomOAuth2User oAuth2User) throws Exception {
        if (oAuth2User == null) {
            log.warn("회원 탈퇴 요청: 인증되지 않은 사용자");
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다.");
        }

        String email = oAuth2User.getEmail();
        log.info("회원 탈퇴 요청: 이메일 = {}", email);
        try {
            userService.deleteUser(email);
            log.info("회원 탈퇴 성공: {}", email);
            return new BaseResponse<>(BaseResponseStatus.OK, "회원 탈퇴가 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            log.warn("회원 탈퇴 실패: {}", e.getMessage());
            return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("회원 탈퇴 처리 중 예외 발생", e);
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR, "회원 탈퇴 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/role")
    public BaseResponse<UserResponse> updateUserRole(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @RequestBody UserRequest userRequest) {
        String email = oAuth2User.getEmail();
        log.info("updateUserRole() 호출됨, 사용자 이메일: {}", email);
        try {
            userService.updateUserRole(email, Role.valueOf(userRequest.getRole()));
        } catch (Exception e) {
            log.error("사용자 역할 업데이트 실패: {}", e.getMessage());
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR, null);
        }
        return new BaseResponse<UserResponse>(BaseResponseStatus.OK, new UserResponse(email, userRequest.getRole()));
    }

    @GetMapping("/mypage")
    public BaseResponse<MyPageResponse> getMyPageInfo(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        if (oAuth2User == null) {
            log.warn("마이페이지 요청: 인증되지 않은 사용자");
            return new BaseResponse<MyPageResponse>(BaseResponseStatus.UNAUTHORIZED);
        }
        try {
            MyPageResponse response = userService.getMyPageInfo(oAuth2User.getEmail());
            return new BaseResponse<>(BaseResponseStatus.OK, response);
        } catch (IllegalArgumentException e) {
            log.warn("마이페이지 요청 실패: {}", e.getMessage());
            return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("마이페이지 요청 중 서버 오류 발생", e);
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
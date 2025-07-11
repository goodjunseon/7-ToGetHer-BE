package com.together.backend.domain.user.controller;

import com.together.backend.domain.user.model.request.UserRequest;
import com.together.backend.domain.user.model.response.MyPageResponse;
import com.together.backend.domain.user.model.response.UserResponse;
import com.together.backend.domain.user.service.UserDeleteService;
import com.together.backend.domain.user.service.UserProfileService;
import com.together.backend.global.common.BaseResponse;
import com.together.backend.global.common.BaseResponseStatus;
import com.together.backend.global.security.jwt.util.CookieUtil;
import com.together.backend.global.security.oauth2.dto.CustomOAuth2User;
import com.together.backend.domain.user.service.UserAuthService;
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

    private final UserAuthService userService;
    private final UserProfileService userProfileService;
    private final UserDeleteService userDeleteService;

    @PostMapping("/info")
    public BaseResponse<String> postUserInfo(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @RequestBody UserRequest userRequest) {
        if (oAuth2User == null) {
            log.warn("회원 정보 저장: 인증되지 않은 사용자");
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED);
        }

        String email = oAuth2User.getEmail();

        try {
            userProfileService.postUserInfo(email ,userRequest);
            return new BaseResponse<>(BaseResponseStatus.OK);
        } catch (IllegalArgumentException e) {
            log.warn("사용자 정보 저장 실패: {}", e.getMessage());
            return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.warn("사용자 정보 저장 중 알 수 없는 오류");
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/mypage")
    public BaseResponse<MyPageResponse> getMyPageInfo(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        if (oAuth2User == null) {
            log.warn("마이페이지 요청: 인증되지 않은 사용자");
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED);
        }
        try {
            MyPageResponse response = userProfileService.getMyPageInfo(oAuth2User.getEmail());
            return new BaseResponse<>(BaseResponseStatus.OK, response);
        } catch (IllegalArgumentException e) {
            log.warn("마이페이지 요청 실패: {}", e.getMessage());
            return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("마이페이지 요청 중 서버 오류 발생", e);
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public BaseResponse<UserResponse> getUserInfo(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        if (oAuth2User == null) {
            log.warn("회원 조회 요청: 인증되지 않은 사용자");
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED);
        }
        String email = oAuth2User.getEmail();
        try {
            UserResponse response = userProfileService.getUserInfo(email);
            return new BaseResponse<>(BaseResponseStatus.OK, response);
        } catch (IllegalArgumentException e) {
            log.warn("회원 정보 조회: {}", e.getMessage());
            return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.warn("회원 정보 조회 중 서버 오류 발생,", e);
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

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
            userService.logout(accessToken); // 블랙리스트 등록, RefreshToken 제거
            // 브라우저 쿠키 직접 만료 처리
            CookieUtil.expireCookie(response, "accessToken");
            CookieUtil.expireCookie(response, "refreshToken");

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
            userDeleteService.deleteUser(email);
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



}
package com.together.backend.domain.user.controller;

import com.together.backend.domain.user.model.response.mainpageinfo.PartnerInfoResponse;
import com.together.backend.domain.user.model.response.mainpageinfo.PillInfoResponse;
import com.together.backend.domain.user.model.response.mainpageinfo.UserInfoResponse;
import com.together.backend.domain.user.service.MainPageService;
import com.together.backend.global.common.BaseResponse;
import com.together.backend.global.common.BaseResponseStatus;
import com.together.backend.global.security.oauth2.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user/home")
@RequiredArgsConstructor
public class MainPageController {

    private final MainPageService mainPageService;

    @GetMapping("/user-info")
    public BaseResponse<UserInfoResponse> getUserInfo(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        if (oAuth2User == null) {
            log.warn("UserInfo 요청: 인증되지 않은 사용자");
            return new BaseResponse<UserInfoResponse>(BaseResponseStatus.UNAUTHORIZED);
        }
        try {
            String email = oAuth2User.getEmail(); // 유저 이메일
            UserInfoResponse response = mainPageService.getUserInfo(email);
            return new BaseResponse<>(BaseResponseStatus.OK, response);
        } catch (IllegalArgumentException e) {
            log.error("@@@@{}@@@@",e.getMessage());
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/partner-info")
    public BaseResponse<PartnerInfoResponse> getPartnerInfo(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @RequestParam("email") String email) {
        if (oAuth2User == null) {
            log.warn("PartnerInfo 요청: 인증되지 않은 사용자");
            return new BaseResponse<PartnerInfoResponse>(BaseResponseStatus.UNAUTHORIZED);
        }
        try {
            PartnerInfoResponse response = mainPageService.getPartnerInfo(email);
            return new BaseResponse<>(BaseResponseStatus.OK,response);
        } catch (IllegalArgumentException e) {
            log.error("@@@@{}@@@@",e.getMessage());
            return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/userpill-info")
    public BaseResponse<PillInfoResponse> getUserPillInfo(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @RequestParam("email") String email) {
        if (oAuth2User == null) {
            log.warn("UserPillInfo 요청: 인증되지 않은 사용자");
            return new BaseResponse<PillInfoResponse>(BaseResponseStatus.UNAUTHORIZED);
        }
        try {
            PillInfoResponse response = mainPageService.getPillInfo(email);
            return new BaseResponse<>(BaseResponseStatus.OK,response);
        } catch (IllegalArgumentException e) {
            log.error("@@@@{}@@@@",e.getMessage());
            return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST);
        }
    }

}

package com.together.backend.domain.sharing.controller;

import com.together.backend.domain.sharing.model.request.ConfirmRequest;
import com.together.backend.domain.sharing.model.request.SaveUrlRequest;
import com.together.backend.domain.sharing.model.response.ConfirmResponse;
import com.together.backend.domain.sharing.model.response.SaveUrlResponse;
import com.together.backend.domain.sharing.service.SharingService;
import com.together.backend.global.common.BaseResponse;
import com.together.backend.global.common.BaseResponseStatus;
import com.together.backend.global.security.oauth2.dto.CustomOAuth2User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user/sharing")
public class SharingController {

    private final SharingService sharingService;

    @PostMapping("/url")
    public BaseResponse<SaveUrlResponse> saveSharedUrl(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @Valid @RequestBody SaveUrlRequest request) {
        if (oAuth2User == null) {
            log.info("인증되지 않은 사용자 요청: {}", request);
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED);
        }

        String email = oAuth2User.getEmail(); // 사용자 이메일(여성)
        try {
            SaveUrlResponse saveUrlResponse = sharingService.saveUrl(email,request.getSharedUrl());
            return new BaseResponse<>(BaseResponseStatus.OK, saveUrlResponse);
        } catch (IllegalArgumentException e) {
            log.warn("공유 URL 저장 실패: {}", e.getMessage());
            return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST,null);
        }
    }

    @PostMapping("/confirm")
    public BaseResponse<ConfirmResponse> confirmInvitaion(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @Valid @RequestBody ConfirmRequest request) {
        if (oAuth2User == null) {
            log.warn("인증되지 않은 사용자 요청: {}", request);
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED);
        }
        log.info("@@@@@@@@@@@@@ {} @@@@@@@@@@@@@@", oAuth2User.getUserId());

        // 나중에 GlobalExceptionHandler 리팩토링 가능
        try {
            Long accepterId = oAuth2User.getUserId(); // 남성 Id : accepter
            String inviterEmail = request.getUserEmail(); // 여성 email : inviter

            ConfirmResponse response = sharingService.confirm(inviterEmail, accepterId);
            return new BaseResponse<>(BaseResponseStatus.OK, response);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            log.warn("상태 오류: {}", e.getMessage());
            return new BaseResponse<>(BaseResponseStatus.CONFLICT);
        } catch (Exception e) {
            log.error("알 수 없는 서버 오류 발생", e);
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }

    }
}

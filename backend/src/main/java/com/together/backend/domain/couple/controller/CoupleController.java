package com.together.backend.domain.couple.controller;

import com.together.backend.domain.couple.model.request.ConnectRequest;
import com.together.backend.domain.couple.model.response.ConnectResponse;
import com.together.backend.domain.couple.model.response.CoupleResponse;
import com.together.backend.domain.couple.service.CoupleService;
import com.together.backend.global.common.BaseResponse;
import com.together.backend.global.common.BaseResponseStatus;
import com.together.backend.global.security.oauth2.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/partner")
public class CoupleController {

    private final CoupleService coupleService;

    @GetMapping
    public BaseResponse<CoupleResponse> getPartnerInfo(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @RequestParam("email") String email) {
        if (oAuth2User == null) {
            log.error("인증되지 않은 사용자 요청입니다.");
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED);
        }

        log.info("파트너 정보 요청: 이메일 = {}", email);

        CoupleResponse response = coupleService.getPartnerInfo(email);

        return new BaseResponse<>(BaseResponseStatus.OK, response);

    }

    @PostMapping("/connect")
    public BaseResponse<ConnectResponse> connectPartner(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @RequestBody ConnectRequest request) {
        if (oAuth2User == null) {
            log.error("인증되지 않은 사용자 요청입니다.");
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED);
        }

        String email = oAuth2User.getEmail();
        log.info("파트너 연결 요청: 이메일 = {}, 파트너 이메일 = {}", email, request.getPartnerEmail());

        ConnectResponse response = coupleService.connectPartner(email, request.getPartnerEmail());

        return new BaseResponse<>(BaseResponseStatus.OK, response);
    }

}

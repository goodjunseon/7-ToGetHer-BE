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
@RequestMapping("/api/user/couple")
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

        String partnerEmail = oAuth2User.getEmail();
        String userEmail = request.getUserEmail();
        log.info("파트너 연결 요청: 사용자 이메일 = {}, 파트너 이메일 = {}", userEmail, partnerEmail);

        try {
            ConnectResponse response = coupleService.connectPartner(userEmail, partnerEmail);
            return new BaseResponse<>(BaseResponseStatus.OK, response);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 값 요청", e);
            return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            log.warn("잘못된 연결 요청", e);
            return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.warn("알 수 없는 오류 발생", e);
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }

    }

}

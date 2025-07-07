package com.together.backend.domain.partner;

import com.together.backend.domain.partner.model.request.PartnerRequest;
import com.together.backend.domain.partner.model.response.PartnerResponse;
import com.together.backend.domain.partner.service.PartnerService;
import com.together.backend.global.common.BaseResponse;
import com.together.backend.global.common.BaseResponseStatus;
import com.together.backend.global.security.oauth2.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/partner")
public class PartnerController {

    private final PartnerService partnerService;

    @GetMapping
    public BaseResponse<PartnerResponse> getPartnerInfo(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @RequestParam("email") String email) {
        if (oAuth2User == null) {
            log.error("인증되지 않은 사용자 요청입니다.");
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED);
        }

        log.info("파트너 정보 요청: 이메일 = {}", email);

        PartnerResponse response = partnerService.getPartnerInfo(email);

        return new BaseResponse<>(BaseResponseStatus.OK, response);

    }

}

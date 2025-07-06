package com.together.backend.pill.controller;

import com.together.backend.global.common.BaseResponse;
import com.together.backend.global.common.BaseResponseStatus;
import com.together.backend.global.security.oauth2.dto.CustomOAuth2User;
import com.together.backend.pill.model.IntakeOption;
import com.together.backend.pill.model.request.UserPillRequest;
import com.together.backend.pill.model.response.UserPillResponse;
import com.together.backend.pill.service.UserPillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserPillController {

    private final UserPillService userPillService;

    @PostMapping("user-pill/intake-info")
    public BaseResponse<UserPillResponse> createUserPill(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @RequestBody UserPillRequest dto) {
        if (oAuth2User ==null) {
            log.error("인증되지 않은 사용자 요청: {}", dto);
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED);
        }

        String email = oAuth2User.getEmail(); // 클라이언트 이메일 추출

        IntakeOption intakeOption = userPillService.saveUserPill(dto, email);


        if (intakeOption == null) {
            log.error("잘못된 요청 데이터: {}", dto);
            return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST);
        }

        log.info("사용자 {}의 약 복용 정보 저장 성공: {}", email, intakeOption.getName());
        UserPillResponse response = new UserPillResponse(intakeOption.getName(), dto.getStartDate());
        return new BaseResponse<>(BaseResponseStatus.OK, response);
    }

    @PatchMapping("user-pill/intake-info")
    public BaseResponse<UserPillResponse> updateUserPill(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @RequestBody UserPillRequest dto) {
        if (oAuth2User == null) {
            log.error("인증되지 않은 사용자 요청: {}", dto);
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED);
        }
        String email = oAuth2User.getEmail(); // 클라이언트 이메일 추출
        try {
            IntakeOption intakeNewOption = userPillService.updateUserPill(dto, email);
            UserPillResponse response = new UserPillResponse(dto.getOption(), dto.getStartDate());
            log.info("사용자 {}의 약 복용 정보 업데이트 성공: {}", email, intakeNewOption.getName());
            return new BaseResponse<UserPillResponse>(BaseResponseStatus.OK, response);
        } catch (Exception e) {
            log.error("약 복용 정보 업데이트 실패: {}", e.getMessage());
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR, null);
        }


    }
}

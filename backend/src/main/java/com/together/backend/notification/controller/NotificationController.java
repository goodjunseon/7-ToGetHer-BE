package com.together.backend.notification.controller;

import com.together.backend.global.common.BaseResponse;
import com.together.backend.global.common.BaseResponseStatus;
import com.together.backend.global.security.oauth2.dto.CustomOAuth2User;
import com.together.backend.notification.model.intake.request.IntakeRequest;
import com.together.backend.notification.model.intake.response.IntakeResponse;
import com.together.backend.notification.service.NotificationIntakeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class NotificationController {
    private final NotificationIntakeService notificationIntakeService;

    // 피임약 복용 알림 시간 저장
    @PostMapping("/notification-settings/pill-intake-time")
    public BaseResponse<IntakeResponse> savePillIntakeTime(@AuthenticationPrincipal CustomOAuth2User oAuth2User,
                                                           @RequestBody IntakeRequest dto) {
        // oAuth2User가 null인 경우 처리
        if (oAuth2User == null) {
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED, null);
        }
        String email = oAuth2User.getEmail(); // 클라이언트 이메일 추출
        log.info("savePillIntakeTime() 호출됨, 사용자 이메일: {}", email);


        LocalTime intakeTime;
        try {
            // 알림 시간 저장 로직 (예: 데이터베이스에 저장)
            intakeTime = LocalTime.parse(dto.getIntakeTime());
            log.info("사용자 {}의 피임약 복용 알림 시간 저장: {}", email, intakeTime);
        } catch (Exception e){
            log.error("잘못된 시간 형식: {}", dto.getIntakeTime(), e);
            return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST, null);
        }

        IntakeResponse data = notificationIntakeService.createPillIntakeTime(email, intakeTime);
        return new BaseResponse<>(BaseResponseStatus.OK, data);

    }

    @PatchMapping("/notification-settings/pill-intake-time")
    public BaseResponse<IntakeResponse> updatePillIntakeTime(@AuthenticationPrincipal CustomOAuth2User oAuth2User,
                                                             @RequestBody IntakeRequest dto) {
        if (oAuth2User == null) {
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED, null);
        }
        String email = oAuth2User.getEmail(); // 클라이언트 이메일 추출
        log.info("updatePillIntakeTime() 호출됨, 사용자 이메일: {}", email);

        // 알림 시간 업데이트 로직 (예: 데이터베이스에 저장)
        LocalTime intakeTime;

        try {
            intakeTime = LocalTime.parse(dto.getIntakeTime());
            log.info("사용자 {}의 피임약 복용 알림 시간 업데이트: {}", email, intakeTime);
        } catch (Exception e) {
            log.error("잘못된 시간 형식: {}", dto.getIntakeTime(), e);
            return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST, null);
        }

        IntakeResponse data = notificationIntakeService.updatePillIntakeTime(email, intakeTime);
        return new BaseResponse<>(BaseResponseStatus.OK, data);
    }
}

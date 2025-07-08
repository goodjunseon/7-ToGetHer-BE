package com.together.backend.domain.notification.controller;

import com.together.backend.global.common.BaseResponse;
import com.together.backend.global.common.BaseResponseStatus;
import com.together.backend.global.security.oauth2.dto.CustomOAuth2User;
import com.together.backend.domain.notification.model.intake.request.IntakeRequest;
import com.together.backend.domain.notification.model.intake.response.IntakeResponse;
import com.together.backend.domain.notification.service.NotificationIntakeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class NotificationSettingsController {
    private final NotificationIntakeService notificationIntakeService;

    // 피임약 복용 알림 시간 저장
    @PostMapping("/notification-settings/pill-intake-time")
    public BaseResponse<IntakeResponse> savePillIntakeTime(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @RequestBody IntakeRequest request) {
        // oAuth2User가 null인 경우 처리
        if (oAuth2User == null) {
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED, null);
        }

        String email = oAuth2User.getEmail(); // 클라이언트 이메일 추출
        log.info("savePillIntakeTime() 호출됨, 사용자 이메일: {}", email);

        try {
            LocalTime intakeTime = LocalTime.parse(request.getIntakeTime());
            log.info("사용자 {}의 피임약 복용 알림 시간 저장: {}", email, intakeTime);
            IntakeResponse data = notificationIntakeService.createPillIntakeTime(email, intakeTime);
            return new BaseResponse<>(BaseResponseStatus.OK, data);
        } catch (IllegalArgumentException e) {
            log.error("알림 중복 설정:");
            return new BaseResponse<>(BaseResponseStatus.DUPLICATE);
        } catch (DateTimeParseException e) {
            log.error("시간 파싱 오류: {}" , request.getIntakeTime(), e);
            return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST);
        } catch (Exception e){
            log.error("알 수 없는 서버 오류 발생", e);
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/notification-settings/pill-intake-time")
    public BaseResponse<IntakeResponse> updatePillIntakeTime(@AuthenticationPrincipal CustomOAuth2User oAuth2User,
                                                             @RequestBody IntakeRequest request) {
        if (oAuth2User == null) {
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED, null);
        }
        String email = oAuth2User.getEmail(); // 클라이언트 이메일 추출
        log.info("updatePillIntakeTime() 호출됨, 사용자 이메일: {}", email);

        try {
            LocalTime intakeTime = LocalTime.parse(request.getIntakeTime());
            log.info("사용자 {}의 피임약 복용 알림 시간 업데이트: {}", email, intakeTime);
            IntakeResponse data = notificationIntakeService.updatePillIntakeTime(email, intakeTime);
            return new BaseResponse<>(BaseResponseStatus.OK, data);
        } catch (DateTimeParseException e) {
            log.error("시간 파싱 오류: {}" , request.getIntakeTime(), e);
            return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("알 수 없는 서버 오류 발생", e);
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }


    }
}

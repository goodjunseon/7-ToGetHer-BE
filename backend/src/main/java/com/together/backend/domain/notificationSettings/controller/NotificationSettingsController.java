package com.together.backend.domain.notificationSettings.controller;

import com.together.backend.domain.notification.model.NotificationType;
import com.together.backend.domain.notification.model.request.NotificationDayRequest;
import com.together.backend.domain.notification.model.request.NotificationEnabledRequest;
import com.together.backend.domain.notification.model.request.NotificationTimeRequest;
import com.together.backend.domain.notification.model.response.NotificationDayResponse;
import com.together.backend.domain.notification.model.response.NotificationEnabledResponse;
import com.together.backend.domain.notification.model.response.NotificationTimeResponse;
import com.together.backend.global.common.BaseResponse;
import com.together.backend.global.common.BaseResponseStatus;
import com.together.backend.global.security.oauth2.dto.CustomOAuth2User;
import com.together.backend.domain.notificationSettings.service.NotificationSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notification-settings")
public class NotificationSettingsController {
    private final NotificationSettingsService notificationSettingsService;

    // 알림 시간 upsert (생성/수정)
    @PostMapping("/{type}/time")
    public BaseResponse<NotificationTimeResponse> upsertNotificationTime(
            @AuthenticationPrincipal CustomOAuth2User oAuth2User,
            @PathVariable("type") String typeStr,
            @RequestBody NotificationTimeRequest request
    ) {
        if (oAuth2User == null) {
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED, null);
        }

        String email = oAuth2User.getEmail(); // 클라이언트 이메일 추출
        log.info("savePillIntakeTime() 호출됨, 사용자 이메일: {}", email);

        try {
            LocalTime time = LocalTime.parse(request.getTime());
            log.info("사용자 {}의 피임약 복용 알림 시간 저장: {}", email, time);
            NotificationType type = NotificationType.fromApiValue(typeStr);
            NotificationTimeResponse data = notificationSettingsService.upsertNotificationTime(email, type, time);
            return new BaseResponse<>(BaseResponseStatus.OK, data);
        } catch (IllegalArgumentException e) {
            log.error("알림 중복 설정:");
            return new BaseResponse<>(BaseResponseStatus.DUPLICATE);
        } catch (DateTimeParseException e) {
            log.error("시간 파싱 오류: {}", request.getTime(), e);
            return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("알 수 없는 서버 오류 발생", e);
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{type}/enabled")
    public BaseResponse<NotificationEnabledResponse> updateNotificationEnabled(
            @AuthenticationPrincipal CustomOAuth2User oAuth2User,
            @PathVariable("type") String typeStr,
            @RequestBody NotificationEnabledRequest request
    ) {
        if (oAuth2User == null) {
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED, null);
        }
        String email = oAuth2User.getEmail(); // 클라이언트 이메일 추출
        log.info("updatePillIntakeTime() 호출됨, 사용자 이메일: {}", email);

        try {
            NotificationType type = NotificationType.fromApiValue(typeStr);
            NotificationEnabledResponse data = notificationSettingsService.updateNotificationEnabled(email, type, request.getEnabled());
            return new BaseResponse<>(BaseResponseStatus.OK, data);
        } catch (Exception e) {
            log.error("알 수 없는 서버 오류 발생", e);
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{type}/days")
    public BaseResponse<NotificationDayResponse> upsertNotificationDay(
            @AuthenticationPrincipal CustomOAuth2User oAuth2User,
            @PathVariable("type") String typeStr,
            @RequestBody NotificationDayRequest request
    ) {
        if (oAuth2User == null) {
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED, null);
        }
        String email = oAuth2User.getEmail();
        NotificationType type = NotificationType.fromApiValue(typeStr);

        // pill-purchase만 지원 (FE에서 걸러주면 더 좋음)
        if (type != NotificationType.PILL_PURCHASE) {
            return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST, null);
        }

        NotificationDayResponse data = notificationSettingsService.upsertNotificationDay(email, type, request.getDaysBefore());
        return new BaseResponse<>(BaseResponseStatus.OK, data);
    }

    // 알림 설정 단건 조회
    @GetMapping("/{type}")
    public BaseResponse<Object> getNotificationSetting(
            @AuthenticationPrincipal CustomOAuth2User oAuth2User,
            @PathVariable("type") String typeStr
    ) {
        if (oAuth2User == null) {
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED, null);
        }
        String email = oAuth2User.getEmail();
        try {
            NotificationType type = NotificationType.fromApiValue(typeStr);
            Object data = notificationSettingsService.getNotificationSetting(email, type);
            return new BaseResponse<>(BaseResponseStatus.OK, data);
        } catch (Exception e) {
            log.error("알림 설정 조회 오류", e);
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }
}




package com.together.backend.domain.notificationSettings.service;

import com.together.backend.domain.notificationSettings.model.NotificationSettings;
import com.together.backend.domain.notification.model.NotificationType;
import com.together.backend.domain.notification.model.response.NotificationDayResponse;
import com.together.backend.domain.notification.model.response.NotificationEnabledResponse;
import com.together.backend.domain.notification.model.response.NotificationTimeResponse;
import com.together.backend.domain.notificationSettings.repository.NotificationSettingsRepository;
import com.together.backend.domain.user.model.entity.User;
import com.together.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class NotificationSettingsService {
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final UserRepository userRepository;

    public NotificationTimeResponse upsertNotificationTime(String email, NotificationType type, LocalTime time) {

        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // NotificationSetting 엔티티를 user와 NotificationType으로 조회
        NotificationSettings setting = notificationSettingsRepository.findByUserAndType(user, type)
                .orElse(null);

        if(setting == null) {
            // INSERT
            setting = NotificationSettings.builder()
                    .user(user)
                    .type(NotificationType.PILL_INTAKE)
                    .isEnabled(true)
                    .notificationTime(time)
                    .build();
        } else {
            // UPDATE
            setting.setNotificationTime(time);
        }

        notificationSettingsRepository.save(setting);

        return new NotificationTimeResponse(setting.isEnabled(), setting.getNotificationTime().toString());
    }

    // 알림 타입이 pill-purchase일 때
    public NotificationDayResponse upsertNotificationDay(String email, NotificationType type, Integer daysBefore) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        NotificationSettings setting = notificationSettingsRepository.findByUserAndType(user, type)
                .orElse(null);

        if (setting == null) {
            setting = NotificationSettings.builder()
                    .user(user)
                    .type(type)
                    .isEnabled(true)
                    .daysBefore(daysBefore) // 며칠 전인지 저장
                    .build();
        } else {
            setting.setDaysBefore(daysBefore);
        }
        notificationSettingsRepository.save(setting);

        return new NotificationDayResponse(setting.isEnabled(), setting.getDaysBefore());
    }


    // 알림 수신여부 변경
    public NotificationEnabledResponse updateNotificationEnabled(String email, NotificationType type, Boolean enabled) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        NotificationSettings setting = notificationSettingsRepository.findByUserAndType(user, type)
                .orElse(null);

        if (setting == null) {
            // INSERT
            NotificationSettings.NotificationSettingsBuilder builder = NotificationSettings.builder()
                    .user(user)
                    .type(type)
                    .isEnabled(enabled);

            // 타입별로 분기하여 기본값 설정
            if (type == NotificationType.PILL_PURCHASE) {
                builder.daysBefore(5)         // 기본값(예: 5일 전)
                        .notificationTime(null);
            } else if (type == NotificationType.PILL_INTAKE) {
                builder.notificationTime(LocalTime.of(9, 0))
                        .daysBefore(null);
            } else {
                builder.notificationTime(null)
                        .daysBefore(null);
            }
            setting = builder.build();
        } else {
            // UPDATE
            setting.setEnabled(enabled);
        }
        notificationSettingsRepository.save(setting);

        return new NotificationEnabledResponse(setting.isEnabled());
    }

    // 조회
    public Object getNotificationSetting(String email, NotificationType type) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        NotificationSettings setting = notificationSettingsRepository.findByUserAndType(user, type)
                .orElseThrow(() -> new IllegalArgumentException("알림 설정이 존재하지 않습니다."));

        switch (type) {
            case PILL_PURCHASE:
                // 약 구매 알림 (며칠 전)
                return new NotificationDayResponse(
                        setting.isEnabled(),
                        setting.getDaysBefore()
                );
            case PILL_INTAKE:
                // 복용 시간 알림 (시:분)
                return new NotificationTimeResponse(
                        setting.isEnabled(),
                        setting.getNotificationTime() != null ? setting.getNotificationTime().toString() : null
                );
            case EMOTION_UPDATE:
                // 감정 기록 알림 (시간, 활성화)
                return new NotificationTimeResponse(
                        setting.isEnabled(),
                        setting.getNotificationTime() != null ? setting.getNotificationTime().toString() : null
                );
            case PARTNER_REQUEST:
                // 파트너 요청: 시간 설정 필요 없음, enabled만 응답 (혹은 타입에 맞게 추가)
                return new NotificationEnabledResponse(setting.isEnabled());
            default:
                throw new IllegalArgumentException("지원하지 않는 알림 타입: " + type);
        }
    }
}

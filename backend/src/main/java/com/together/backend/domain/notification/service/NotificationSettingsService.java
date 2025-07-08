package com.together.backend.domain.notification.service;

import com.together.backend.domain.notification.model.NotificationSettings;
import com.together.backend.domain.notification.model.NotificationType;
import com.together.backend.domain.notification.model.notification.response.NotificationDayResponse;
import com.together.backend.domain.notification.model.notification.response.NotificationEnabledResponse;
import com.together.backend.domain.notification.model.notification.response.NotificationTimeResponse;
import com.together.backend.domain.notification.repository.NotificationSettingsRepository;
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

    // 시간 +
    public Object getNotificationSetting(String email, NotificationType type) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        NotificationSettings setting = notificationSettingsRepository.findByUserAndType(user, type)
                .orElseThrow(() -> new IllegalArgumentException("알림 설정이 존재하지 않습니다."));

        if (type == NotificationType.PILL_PURCHASE) {
            return new NotificationDayResponse(
                    setting.isEnabled(),
                    setting.getDaysBefore()
            );
        } else if (type == NotificationType.PILL_INTAKE) {
            return new NotificationTimeResponse(
                    setting.isEnabled(),
                    setting.getNotificationTime() != null ? setting.getNotificationTime().toString() : null
            );
        }
        // 기타 타입 처리...
        return null;
    }
}

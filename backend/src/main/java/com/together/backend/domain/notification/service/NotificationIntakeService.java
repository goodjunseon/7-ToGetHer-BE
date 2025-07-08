package com.together.backend.domain.notification.service;

import com.together.backend.domain.notification.model.NotificationSettings;
import com.together.backend.domain.notification.model.NotificationType;
import com.together.backend.domain.notification.model.intake.response.IntakeResponse;
import com.together.backend.domain.notification.model.notification.response.NotificationTimeResponse;
import com.together.backend.domain.notification.repository.NotificationSettingsRepository;
import com.together.backend.domain.user.model.entity.User;
import com.together.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class NotificationIntakeService {
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

}

package com.together.backend.notification.service;

import com.together.backend.notification.model.NotificationSetting;
import com.together.backend.notification.model.NotificationType;
import com.together.backend.notification.model.intake.response.IntakeResponse;
import com.together.backend.notification.repository.NotificationSettingsRepository;
import com.together.backend.user.model.entity.User;
import com.together.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class NotificationIntakeService {
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final UserRepository userRepository;

    public IntakeResponse createPillIntakeTime(String email, LocalTime intakeTime) {

        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        boolean exists = notificationSettingsRepository.findByUserAndType(user, NotificationType.PILL_INTAKE).isPresent();

        if (exists) {
            throw new IllegalArgumentException("이미 알림 설정이 존재합니다.");
        }

        // NotificationSetting 엔티티를 user와 NotificationType으로 조회
        NotificationSetting setting = NotificationSetting.builder()
                .user(user)
                .type(NotificationType.PILL_INTAKE)
                .isEnabled(true)
                .notificationTime(intakeTime)
                .build();

        notificationSettingsRepository.save(setting);

        return new IntakeResponse(setting.isEnabled(), setting.getNotificationTime().toString());
    }

    public IntakeResponse updatePillIntakeTime(String email, LocalTime intakeTime) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        NotificationSetting setting = notificationSettingsRepository.findByUserAndType(user,NotificationType.PILL_INTAKE)
                .orElseThrow(() -> new IllegalArgumentException("알림 설정이 존재하지 않습니다."));

        setting.setNotificationTime(intakeTime);
        notificationSettingsRepository.save(setting);
        return new IntakeResponse(setting.isEnabled(), setting.getNotificationTime().toString());
    }

}

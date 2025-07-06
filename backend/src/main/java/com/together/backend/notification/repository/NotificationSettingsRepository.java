package com.together.backend.notification.repository;

import com.together.backend.notification.model.NotificationSetting;
import com.together.backend.notification.model.NotificationType;
import com.together.backend.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSetting, Long> {
    Optional<NotificationSetting> findByUserAndType(User user, NotificationType type);
}

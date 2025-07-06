package com.together.backend.notification.repository;

import com.together.backend.notification.model.NotificationSettings;
import com.together.backend.notification.model.NotificationType;
import com.together.backend.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {
    Optional<NotificationSettings> findByUserAndType(User user, NotificationType type);
}

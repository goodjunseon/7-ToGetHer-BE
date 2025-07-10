package com.together.backend.domain.notification.repository;

import com.together.backend.domain.notification.model.NotificationSettings;
import com.together.backend.domain.notification.model.NotificationType;
import com.together.backend.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {
    Optional<NotificationSettings> findByUserAndType(User user, NotificationType type);
    List<NotificationSettings> findByTypeAndIsEnabled(NotificationType notificationType, boolean b);
}

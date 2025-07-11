package com.together.backend.domain.notification.repository;

import com.together.backend.domain.notification.model.Notification;
import com.together.backend.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    void deleteByReceiver(User user);

    void deleteBySender(User user);
}

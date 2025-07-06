package com.together.backend.notification.model;

import com.together.backend.user.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notification_table") // 알림 테이블
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId; // 알림 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Enumerated(EnumType.STRING)
    private NotificationType type; // 알림 타입

    private boolean isEnabled; // 알림 활성화 여부

    private LocalTime notificationTime; // 알림 시간 (복용/감정)

}

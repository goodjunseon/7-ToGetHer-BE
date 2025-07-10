package com.together.backend.domain.notification.service;

import com.together.backend.domain.calendar.model.entity.IntakeRecord;
import com.together.backend.domain.calendar.repository.IntakeRecordRepository;
import com.together.backend.domain.notification.model.Notification;
import com.together.backend.domain.notification.model.NotificationSettings;
import com.together.backend.domain.notification.model.NotificationType;
import com.together.backend.domain.notification.repository.NotificationRepository;
import com.together.backend.domain.notification.repository.NotificationSettingsRepository;
import com.together.backend.domain.pill.model.UserPill;
import com.together.backend.domain.pill.repository.UserPillRepository;
import com.together.backend.domain.user.model.entity.User;
import com.together.backend.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final UserPillRepository userPillRepository;
    private final IntakeRecordRepository intakeRecordRepository;
    private final NotificationSseService notificationSseService;

    @Transactional
    public Notification sendNotification(
            Long receiverId,
            Long senderId,
            NotificationType type,
            String title,
            String content
    ) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("수신자(user) 정보가 없습니다"));
        User sender = null;
        if(senderId != null) {
            sender = userRepository.findById(senderId)
                    .orElseThrow(() -> new IllegalArgumentException("발신자(user) 정보가 없습니다."));
        }

        Notification notification = Notification.builder()
                .receiver(receiver)
                .sender(sender)
                .type(type)
                .title(title)
                .content(content)
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        notificationSseService.notifyUser(saved);
        return saved;
    }

    /**
     * 약 복용 알림 - 매 1분마다 현재 시각에 맞는 유저에게 알림 생성
     */
    @Scheduled(cron= "0 * * * * *") // 매분 0초
    public void checkAndSendPillIntakeNotifications() {
        LocalTime now = LocalTime.now().withSecond(0).withNano(0); // 초, 밀리초 버림

        // 알림 설정이 켜져 있는 모든 유저의 PILL_INTAKE 조회
        List<NotificationSettings> settingsList =
                notificationSettingsRepository.findByTypeAndIsEnabled(NotificationType.PILL_INTAKE, true);

        for (NotificationSettings settings : settingsList) {
            // 알림 시간이 null이 아니고, 지금 시간과 같으면
            if (settings.getNotificationTime() != null &&
                    settings.getNotificationTime().withSecond(0).equals(now)) {

                // 유저의 UserPill 가져오기
                Optional<UserPill> userPillOpt = userPillRepository.findByUser(settings.getUser());
                if(userPillOpt.isPresent()) {
                    UserPill userPill = userPillOpt.get();

                    // 오늘 복용했는지 IntakeRecord로 체크
                    Optional<IntakeRecord> recordOpt = intakeRecordRepository
                            .findByUserPillAndIntakeDate(userPill, LocalDate.now());
                    boolean alreadyTaken = recordOpt.map(r -> Boolean.TRUE.equals(r.getIsTaken())).orElse(false);

                    if(alreadyTaken) continue; // 오늘 이미 복용했다면 알림 보내지 않음
                }

                // 알림 저장 (sender는 system)
                sendNotification(
                        settings.getUser().getUserId(),
                        null, // sender == system
                        NotificationType.PILL_INTAKE,
                        "피임약을 복용할 시간이에요.",
                        "피임약을 복용할 시간이 되었어요. 식사 후 충분한 물과 함께 섭취해주세요."
                );
            }
        }
    }

    /**
     * 약 구매 알림 - 매일 10시에 실행, 구매알림 시점이 오늘인 경우 알림 생성
     */
    @Scheduled(cron = "0 0 10 * * *") // 매일 22시 7분
    public void checkAndSendPillPurchaseNotifications() {
        LocalDate today = LocalDate.now();
        System.out.println("[구매알림] 오늘 날짜: " + today);

        List<NotificationSettings> settingsList =
                notificationSettingsRepository.findByTypeAndIsEnabled(NotificationType.PILL_PURCHASE, true);

        for (NotificationSettings settings : settingsList) {
            System.out.println("[구매알림] 검사중 user: " + settings.getUser().getUserId());

            Optional<UserPill> userPillOpt = userPillRepository.findByUser(settings.getUser());
            if(userPillOpt.isPresent()) {
                UserPill userPill = userPillOpt.get();

                System.out.println("[구매알림] nextPurchaseAlert: " + userPill.getNextPurchaseAlert());

                // today == nextPurchaseAlert일 때만 알림 전송
                if(today.equals(userPill.getNextPurchaseAlert())) {
                    System.out.println("[구매알림] 알림 생성! 유저: " + settings.getUser().getUserId());
                    sendNotification(
                            settings.getUser().getUserId(),
                            null,
                            NotificationType.PILL_PURCHASE,
                            "피임약을 구매해야 돼요",
                            "님은 피임약이 얼마 남지 않았어요. 약국에 가서 구매 후 기록해주세요."
                    );
                }
            }
        }
    }

}

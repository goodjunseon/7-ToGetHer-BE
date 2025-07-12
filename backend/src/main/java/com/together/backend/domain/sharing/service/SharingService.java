package com.together.backend.domain.sharing.service;

import com.together.backend.domain.couple.repository.CoupleRepository;
import com.together.backend.domain.notification.model.NotificationType;
import com.together.backend.domain.notification.service.NotificationService;
import com.together.backend.domain.sharing.model.entity.Sharing;
import com.together.backend.domain.sharing.model.response.ConfirmResponse;
import com.together.backend.domain.sharing.model.response.SaveUrlResponse;
import com.together.backend.domain.sharing.repository.SharingRepository;
import com.together.backend.domain.user.model.entity.User;
import com.together.backend.domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@AllArgsConstructor
public class SharingService {
    private final SharingRepository sharingRepository;
    private final UserRepository userRepository;
    private final CoupleRepository coupleRepository;
    private final NotificationService notificationService;

    @Transactional
    public SaveUrlResponse saveUrl(String email, String url) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));

        // 여성이 남성에게 요청을 보냈을 때 And 여성이 기존 Sharing 테이블이 없을 때 저장
        Sharing sharing = sharingRepository.findByUser(user).orElseGet(() -> Sharing.create(user,null, url));
        sharing.updateUrl(url);

        Sharing saved = sharingRepository.save(sharing);

        return SaveUrlResponse.from(saved);
    }

    @Transactional
    public ConfirmResponse confirm(String inviterEmail, Long accepterId) {
        log.info("[CONFIRM] 시작: inviterEmail={}, accepterId={}", inviterEmail, accepterId);

        // 1. 초대자 조회(사용자: 여성)
        User inviter = userRepository.findByEmail(inviterEmail)
                .orElseThrow(() -> {
                    log.warn("[CONFIRM] 초대자 없음: inviterEmail={}", inviterEmail);
                    return new IllegalArgumentException("초대자를 찾을 수 없습니다.");
                });
        log.info("[CONFIRM] 초대자 조회 성공: inviterId={}, email={}", inviter.getUserId(), inviter.getEmail());

        // 2. 수락자 조회 (파트너: 남성)
        User accepter = userRepository.findById(accepterId)
                .orElseThrow(() -> {
                    log.warn("[CONFIRM] 수락자 없음: accepterId={}", accepterId);
                    return new IllegalArgumentException("수락자를 찾을 수 없습니다.");
                });
        log.info("[CONFIRM] 수락자 조회 성공: accepterId={}, email={}", accepter.getUserId(), accepter.getEmail());

        // 3. 공유 조회
        Sharing sharing = sharingRepository.findByUser(inviter)
                .orElseThrow(() -> {
                    log.warn("[CONFIRM] 공유 정보 없음: inviterId={}", inviter.getUserId());
                    return new IllegalStateException("공유 정보가 존재하지 않습니다.");
                });
        log.info("[CONFIRM] 공유 조회 성공");

        // 4. 이미 수락된 초대
        if (sharing.isConfirmed()) {
            log.warn("[CONFIRM] 이미 수락된 초대");
            throw new IllegalStateException("이미 수락된 초대입니다.");
        }

        // 5. Couple 중복 생성 방지
        boolean coupleExists = coupleRepository.existsByUserOrPartnerUserId(inviter, accepter.getUserId());
        if (coupleExists) {
            log.warn("[CONFIRM] 이미 커플 등록: inviterId={}, accepterId={}", inviter.getUserId(), accepter.getUserId());
            throw new IllegalStateException("이미 커플로 등록된 사용자입니다.");
        }

        log.info("[CONFIRM] sharing.confirm() 실행");
        sharing.confirm();
        sharingRepository.save(sharing); // 명시적 저장

        // 6. 알림 서비스 호출
        log.info("[CONFIRM] 알림 발송: fromUser={}, toUser={}", accepter.getUserId(), inviter.getUserId());
        notificationService.sendNotification(
                inviter.getUserId(),          // 알림 받을 사람: 초대자(여성)
                accepter.getUserId(),         // 알림 보낸 사람: 수락자(남성)
                NotificationType.PARTNER_REQUEST, // 혹은 PARTNER_CONFIRMED
                "파트너 연동이 수락되었습니다.",
                accepter.getNickname() + "님이 파트너 요청을 수락했습니다!"
        );

        log.info("[CONFIRM] 완료");
        return ConfirmResponse.from(sharing);
    }

}

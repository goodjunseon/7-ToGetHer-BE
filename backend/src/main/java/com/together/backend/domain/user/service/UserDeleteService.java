package com.together.backend.domain.user.service;

import com.together.backend.domain.calendar.repository.BasicRecordRepository;
import com.together.backend.domain.calendar.repository.IntakeRecordRepository;
import com.together.backend.domain.calendar.repository.RelationRecordRepository;
import com.together.backend.domain.couple.repository.CoupleRepository;
import com.together.backend.domain.notification.repository.NotificationRepository;
import com.together.backend.domain.pill.model.UserPill;
import com.together.backend.domain.pill.repository.UserPillRepository;
import com.together.backend.domain.sharing.repository.SharingRepository;
import com.together.backend.domain.user.model.entity.User;
import com.together.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDeleteService {

    private final UserRepository userRepository;
    private final SharingRepository sharingRepository;
    private final BasicRecordRepository basicRecordRepository;
    private final IntakeRecordRepository intakeRecordRepository;
    private final UserPillRepository userPillRepository;
    private final NotificationRepository notificationRepository;
    private final RelationRecordRepository relationRecordRepository;
    private final CoupleRepository coupleRepository;

    @Transactional
    public void deleteUser(String email) {
        log.info("회원 탈퇴 요청 이메일 {}", email);

        User user = userRepository.findByEmail(email).orElseThrow( () -> {
            log.warn("회원 탈퇴 실패 - 사용자를 찾을 수 없음: {} ", email);
            return new IllegalArgumentException("해당 이메일의 사용자를 찾을 수 없습니다.");
        });

        removeAllRelatedEntities(user);
        userRepository.delete(user);

        log.info("회원 탈퇴 완료: {}", user.getEmail());
    }

    private void removeAllRelatedEntities(User user) {
        List<UserPill> userPills = userPillRepository.findAllByUser(user);
        for (UserPill pill : userPills) {
            intakeRecordRepository.deleteByUserPill(pill); // 1) intake_info 삭제 (user_pill 삭제 하기 위해)
        }
        userPillRepository.deleteAll(userPills); // 2) user_pill 삭제

        relationRecordRepository.deleteByUser(user); // 3) relation_record 삭제
        coupleRepository.deleteByUser(user); // 4) couple 삭제
        sharingRepository.deleteByUser(user);
        basicRecordRepository.deleteByUser(user);
        notificationRepository.deleteByReceiver(user);
        notificationRepository.deleteBySender(user);
    }
}

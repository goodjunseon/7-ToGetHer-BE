package com.together.backend.domain.user.service;

import com.together.backend.domain.calendar.repository.BasicRecordRepository;
import com.together.backend.domain.calendar.repository.IntakeRecordRepository;
import com.together.backend.domain.calendar.repository.RelationRecordRepository;
import com.together.backend.domain.couple.repository.CoupleRepository;
import com.together.backend.domain.notification.repository.NotificationRepository;
import com.together.backend.domain.pill.model.UserPill;
import com.together.backend.domain.pill.repository.UserPillRepository;
import com.together.backend.domain.sharing.repository.SharingRepository;
import com.together.backend.domain.user.model.entity.Role;
import com.together.backend.domain.user.model.response.MyPageResponse;
import com.together.backend.global.security.jwt.service.BlackListTokenService;
import com.together.backend.global.security.jwt.service.JwtTokenService;
import com.together.backend.domain.user.model.entity.User;
import com.together.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final BlackListTokenService blackListTokenService;
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    // <-- UserDependencies -->
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

        deleteUserDependencies(user);
        userRepository.delete(user);

        log.info("회원 탈퇴 완료: {}", user.getEmail());
    }

    public void logout(String accessToken) {
        // 블랙리스트에 토큰 추가
        blackListTokenService.blackListTokenSave(accessToken);
        log.info("blackListTokenService.blackListTokenSave() 호출");

        // 리프레시 토큰 삭제
        jwtTokenService.refreshTokenDelete(accessToken);
        log.info("jwtTokenService.refreshTokenDelete() 호출");
    }

    public void updateUserRole(String email, Role role) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            log.warn("사용자를 찾을 수 없습니다: {}", email);
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        User user = optionalUser.get();
        user.setRole(role);
        userRepository.save(user);
        log.info("사용자 역할 업데이트 완료: {}, 새로운 역할: {}", email, role);
    }

    public MyPageResponse getMyPageInfo(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다: "));
        return new MyPageResponse(user.getNickname(), user.getEmail(), user.getProfileImageUrl());
    }

    private void deleteUserDependencies(User user) {
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

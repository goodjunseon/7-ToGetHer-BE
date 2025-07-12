package com.together.backend.domain.couple.service;

import com.together.backend.domain.couple.model.entity.Couple;
import com.together.backend.domain.couple.model.response.ConnectResponse;
import com.together.backend.domain.couple.model.response.CoupleResponse;
import com.together.backend.domain.couple.repository.CoupleRepository;
import com.together.backend.domain.sharing.model.entity.Sharing;
import com.together.backend.domain.sharing.repository.SharingRepository;
import com.together.backend.domain.user.model.entity.User;
import com.together.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoupleService {

    private final CoupleRepository coupleRepository;
    private final UserRepository userRepository;
    private final SharingRepository sharingRepository;

    // 파트너 정보를 가져오는 메소드
    public CoupleResponse getPartnerInfo(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));
        log.info("파트너 정보 조회: 이메일 = {}", email);

        return new CoupleResponse(user.getNickname(), user.getProfileImageUrl());

    }

    public ConnectResponse connectPartner(String userEmail, String partnerEmail) {
        // 사용자 & 파트너 조회
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userEmail));
        Long partnerUserId = userRepository.findByEmail(partnerEmail).orElseThrow(() -> new IllegalArgumentException("파트너를 찾을 수 없습니다: " + partnerEmail)).getUserId();

        // Sharing 상태 확인
        Sharing sharing = sharingRepository.findByUser(user).orElseThrow(() -> new IllegalStateException("공유 정보가 존재하지 않습니다."));

        if (!sharing.isShared()) {
            throw new IllegalStateException("아직 초대가 공유되지 않았습니다.");
        }
        if (!sharing.isConfirmed()) {
            throw new IllegalStateException("아직 파트너가 초대를 수락하지 않았습니다.");
        }

        // Couple 중복 여부 확인 -> 이부분은 isConfirmed가 true이면 무조건 true이지 않을까? 2차 검증 불필요 한가?
        boolean coupleExists = coupleRepository.existsByUserOrPartnerUserId(user, partnerUserId);
        if (coupleExists) {
            throw new IllegalStateException("이미 커플로 등록된 사용자입니다.");
        }

        // Couple 생성
        Couple couple = Couple.builder()
                .user(user)
                .partnerUserId(partnerUserId)
                .connectedAt(LocalDateTime.now())
                .isConnected(true)
                .build();

        log.info("partner 객체 생성: userEmail = {}, partnerEmail = {}, isConnected = {}", userEmail, partnerEmail, couple.getIsConnected());
        coupleRepository.save(couple);
        return new ConnectResponse(couple.getUser().getNickname(), couple.getUser().getProfileImageUrl());

    }
}

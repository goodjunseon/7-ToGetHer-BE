package com.together.backend.domain.couple.service;

import com.together.backend.domain.couple.model.entity.Couple;
import com.together.backend.domain.couple.model.entity.CoupleStatus;
import com.together.backend.domain.couple.model.response.ConnectResponse;
import com.together.backend.domain.couple.model.response.CoupleResponse;
import com.together.backend.domain.couple.repository.CoupleRepository;
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

    // 파트너 정보를 가져오는 메소드
    public CoupleResponse getPartnerInfo(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));
        log.info("파트너 정보 조회: 이메일 = {}", email);

        return new CoupleResponse(user.getNickname(), user.getProfileImageUrl());

    }

    public ConnectResponse connectPartner(String userEmail, String partnerEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userEmail));
        Long partnerUserId = userRepository.findByEmail(partnerEmail).orElseThrow(() -> new RuntimeException("파트너를 찾을 수 없습니다: " + partnerEmail)).getUserId();

        Couple couple = Couple.builder()
                .user(user)
                .partnerUserId(partnerUserId)
                .connectedAt(LocalDateTime.now())
                .status(CoupleStatus.CONNECT)
                .build();

        log.info("partner 객체 생성: userEmail = {}, partnerEmail = {}, status = {}", userEmail, partnerEmail, couple.getStatus());
        coupleRepository.save(couple);
        return new ConnectResponse(couple.getUser().getNickname(), couple.getUser().getProfileImageUrl());

    }
}

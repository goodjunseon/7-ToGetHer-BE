package com.together.backend.domain.partner.service;

import com.together.backend.domain.partner.model.response.PartnerResponse;
import com.together.backend.domain.partner.repository.PartnerRepository;
import com.together.backend.domain.user.model.entity.User;
import com.together.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final UserRepository userRepository;

    // 파트너 정보를 가져오는 메소드
    public PartnerResponse getPartnerInfo(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));
        log.info("파트너 정보 조회: 이메일 = {}", email);

        return new PartnerResponse(user.getNickname(), user.getProfileImageUrl());

    }

}

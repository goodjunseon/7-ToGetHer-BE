package com.together.backend.domain.user.service;

import com.together.backend.domain.couple.model.entity.Couple;
import com.together.backend.domain.couple.repository.CoupleRepository;
import com.together.backend.domain.pill.model.UserPill;
import com.together.backend.domain.pill.repository.UserPillRepository;
import com.together.backend.domain.user.model.entity.User;
import com.together.backend.domain.user.model.response.mainpageinfo.PartnerInfoResponse;
import com.together.backend.domain.user.model.response.mainpageinfo.PillInfoResponse;
import com.together.backend.domain.user.model.response.mainpageinfo.UserInfoResponse;
import com.together.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
@Setter
@RequiredArgsConstructor
@Service
public class MainPageService {

    private final UserRepository userRepository;
    private final CoupleRepository coupleRepository;
    private final UserPillRepository userPillRepository;

    private User getOrThrow(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }


    // 사용자의 이메일 받고 사용자 정보를 전달함
    public UserInfoResponse getUserInfo(String email) {
        User user = getOrThrow(email);
        return new UserInfoResponse(user.getEmail(), user.getNickname(), user.getProfileImageUrl());
    }

    public PartnerInfoResponse getPartnerInfo(String email) {
        User user = getOrThrow(email);
        Optional<Couple> optCouple = coupleRepository.findByUser(user);

        if (optCouple.isPresent()) {
            Couple couple = optCouple.get();
            // 연결이 안됐거나 날짜 없을 때 -> 0일 또는 Null로 처리해야함
            if (Boolean.TRUE.equals(couple.getIsConnected()) && couple.getConnectedAt() != null) {
                long daysTogether = ChronoUnit.DAYS.between(couple.getConnectedAt().toLocalDate(), LocalDate.now());

                // 파트너 조회
                String partnerNickname = userRepository.findByUserId(couple.getPartnerUserId()).map(User::getNickname).orElse(null);
                return new PartnerInfoResponse(partnerNickname,true, daysTogether);
            } else {
                //연결 안됨
                return new PartnerInfoResponse(null, false, 0L);
            }
        } else {
            // 커플 관계 자체가 없을 때
            return new PartnerInfoResponse(null, false, 0L);
        }

    }

    public PillInfoResponse getPillInfo(String email) {
        User user = getOrThrow(email);
        Optional<UserPill> optPill = userPillRepository.findTopByUserOrderByStartDateDesc(user);

        long daysOnPill = optPill.map(pill -> ChronoUnit.DAYS.between(pill.getStartDate(), LocalDate.now())).orElse(0L);
        return new PillInfoResponse(daysOnPill);
    }



}

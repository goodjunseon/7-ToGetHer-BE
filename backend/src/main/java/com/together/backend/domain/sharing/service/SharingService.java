package com.together.backend.domain.sharing.service;

import com.together.backend.domain.couple.repository.CoupleRepository;
import com.together.backend.domain.sharing.model.Sharing;
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

    public SaveUrlResponse saveUrl(String email, String url) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));

        // 여성이 남성에게 요청을 보냈을 때 And 여성이 기존 Sharing 테이블이 없을 때 저장
        Sharing sharing = sharingRepository.findByUser(user).orElseGet(() -> Sharing.create(user,null, url));
        sharing.updateUrl(url);

        Sharing saved = sharingRepository.save(sharing);

        return new SaveUrlResponse(saved.getSharedUrl(), saved.isShared(), saved.getSharedAt());
    }

    @Transactional
    public ConfirmResponse confirm(String inviterEmail, Long accepterId) {
        // 초대자 조회(사용자: 여성)
        User inviter = userRepository.findByEmail(inviterEmail).orElseThrow(() -> new IllegalArgumentException("초대자를 찾을 수 없습니다."));

        // 수락자 조회 (파트너: 남성)
        User accepter = userRepository.findById(accepterId).orElseThrow(() -> new IllegalArgumentException("수락자를 찾을 수 없습니다."));

        // 공유 조회
        Sharing sharing = sharingRepository.findByUser(inviter).orElseThrow(() -> new IllegalStateException("공유 정보가 존재하지 않습니다."));

        if (sharing.isConfirmed()) {
            throw new IllegalStateException("이미 수락된 초대입니다.");
        }

        // Couple 중복 생성 방지, 추후 메서드 분리 고려
        boolean coupleExists = coupleRepository.existsByUserOrPartnerUserId(inviter, accepter.getUserId());
        if (coupleExists) {
            throw new IllegalStateException("이미 커플로 등록된 사용자입니다.");
        }

        sharing.confirm();
        sharingRepository.save(sharing); // 명시적 저장

        return  new ConfirmResponse(sharing.isConfirmed(), sharing.getConfirmedAt());
    }
}

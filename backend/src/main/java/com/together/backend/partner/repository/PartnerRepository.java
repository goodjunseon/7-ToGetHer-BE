package com.together.backend.partner.repository;

import com.together.backend.partner.model.entity.Partner;
import java.util.Optional;

public interface PartnerRepository {
    Optional<Partner> findByUser_UserId(Long userId);
    // userId와 partnerUserId 조합으로 찾기
    Optional<Partner> findByUser_UserIdAndPartnerUserId(Long userId, Long partnerUserId);
}

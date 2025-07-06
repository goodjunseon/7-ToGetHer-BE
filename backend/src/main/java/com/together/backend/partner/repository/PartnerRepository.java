package com.together.backend.partner.repository;

import com.together.backend.partner.model.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartnerRepository extends JpaRepository<Partner, Long> {
    Optional<Partner> findByUser_UserId(Long userId);
    // userId와 partnerUserId 조합으로 찾기
    Optional<Partner> findByUser_UserIdAndPartnerUserId(Long userId, Long partnerUserId);
}

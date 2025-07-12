package com.together.backend.domain.couple.repository;

import com.together.backend.domain.couple.model.entity.Couple;
import com.together.backend.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoupleRepository extends JpaRepository<Couple, Long> {
    Optional<Couple> findByUser_UserId(Long userId);
    Optional<Couple> findByPartnerUserId(Long partnerUserId);
    Optional<Couple> findByUser_UserIdAndPartnerUserId(Long userId, Long partnerUserId);
    Optional<Couple> findByUser(User user);
    boolean existsByUserOrPartnerUserId(User inviter, Long userId);
    // userId와 partnerUserId 조합으로 찾기
    Optional<Couple> findById(Long coupleId);

    void deleteByUser(User user);
}

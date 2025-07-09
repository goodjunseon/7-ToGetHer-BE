package com.together.backend.domain.pill.repository;

import com.together.backend.domain.pill.model.UserPill;
import com.together.backend.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPillRepository extends JpaRepository<UserPill, Long> {
    Optional<UserPill> findByUser(User user);
    Optional<UserPill> findByUser_UserId(Long userId);
    Optional<UserPill> findTopByUserOrderByStartDateDesc(User user); // 가장 최신의 startDate 값 가져오기

    List<UserPill> findAllByUser(User user);
}
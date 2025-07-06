package com.together.backend.pill.repository;

import com.together.backend.pill.model.UserPill;
import com.together.backend.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPillRepository extends JpaRepository<UserPill, Long> {
    Optional<UserPill> findByUser(User user);
}

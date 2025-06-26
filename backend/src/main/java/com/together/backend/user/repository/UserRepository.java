package com.together.backend.user.repository;

import com.together.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findBySocialId(String socialId);
}
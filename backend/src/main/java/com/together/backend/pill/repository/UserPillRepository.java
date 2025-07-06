package com.together.backend.pill.repository;

import com.together.backend.pill.model.UserPill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPillRepository extends JpaRepository<UserPill, Long> {
}

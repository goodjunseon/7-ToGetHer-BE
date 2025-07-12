package com.together.backend.domain.calendar.repository;

import com.together.backend.domain.calendar.model.entity.IntakeRecord;
import com.together.backend.domain.pill.model.entity.UserPill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IntakeRecordRepository extends JpaRepository<IntakeRecord, Long> {
    Optional<IntakeRecord> findByUserPillAndIntakeDate(UserPill userPill, LocalDate intakeDate);
    void deleteByUserPill(UserPill userPill);
    // UserPill로 모든 IntakeRecord 조회
    List<IntakeRecord> findByUserPill(UserPill userPill);
}

package com.together.backend.calendar.repository;

import com.together.backend.calendar.model.entity.IntakeRecord;
import com.together.backend.pill.model.UserPill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface IntakeRecordRepository extends JpaRepository<IntakeRecord, Long> {
    Optional<IntakeRecord> findByUserPillAndIntakeDate(UserPill userPill, LocalDate intakeDate);

}

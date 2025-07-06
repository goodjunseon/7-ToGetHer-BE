package com.together.backend.calendar.repository;

import com.together.backend.calendar.model.entity.IntakeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntakeRecordRepository extends JpaRepository<IntakeRecord, Long> {
}

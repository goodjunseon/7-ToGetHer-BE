package com.together.backend.domain.calendar.repository;

import com.together.backend.domain.calendar.model.entity.BasicRecord;
import com.together.backend.domain.calendar.model.entity.IntakeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BasicRecordRepository extends JpaRepository<BasicRecord, Long> {
    Optional<BasicRecord> findByUserEmailAndOccuredAt(String email, LocalDateTime occuredAt);
    Optional<BasicRecord> findByIntakeRecord(IntakeRecord intakeRecord);

}

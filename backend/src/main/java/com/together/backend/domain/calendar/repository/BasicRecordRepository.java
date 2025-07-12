package com.together.backend.domain.calendar.repository;

import com.together.backend.domain.calendar.model.entity.BasicRecord;
import com.together.backend.domain.calendar.model.entity.IntakeRecord;
import com.together.backend.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BasicRecordRepository extends JpaRepository<BasicRecord, Long> {
    Optional<BasicRecord> findByUserEmailAndOccuredAt(String email, LocalDateTime occuredAt);
    Optional<BasicRecord> findByIntakeRecord(IntakeRecord intakeRecord);
    List<BasicRecord> findAllByUserAndOccuredAtBetween(User user, LocalDateTime start, LocalDateTime end);
    // IntakeRecord로 연결된 BasicRecord 모두 삭제 (custom 쿼리, bulk delete)
    void deleteByIntakeRecord(IntakeRecord intakeRecord);

    void deleteByUser(User user);
}

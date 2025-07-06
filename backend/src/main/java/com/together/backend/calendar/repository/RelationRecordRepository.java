package com.together.backend.calendar.repository;

import com.together.backend.calendar.model.entity.RelationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface RelationRecordRepository extends JpaRepository<RelationRecord, Long> {
    Optional<RelationRecord> findByUserEmailAndRecordDate(String email, LocalDate recordDate);
}

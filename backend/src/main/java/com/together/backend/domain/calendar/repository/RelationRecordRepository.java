package com.together.backend.domain.calendar.repository;

import com.together.backend.domain.calendar.model.entity.RelationRecord;
import com.together.backend.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface RelationRecordRepository extends JpaRepository<RelationRecord, Long> {
    Optional<RelationRecord> findByUserEmailAndRecordDate(String email, LocalDate recordDate);
    Optional<RelationRecord> findByUserAndPartnerAndRecordDate(User user, User partner, LocalDate recordDate);
}

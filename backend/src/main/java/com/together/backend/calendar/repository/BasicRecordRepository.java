package com.together.backend.calendar.repository;

import com.together.backend.calendar.model.entity.BasicRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasicRecordRepository extends JpaRepository<BasicRecord, Long> {
}

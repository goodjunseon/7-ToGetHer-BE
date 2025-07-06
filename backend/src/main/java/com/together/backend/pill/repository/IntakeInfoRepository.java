package com.together.backend.pill.repository;

import com.together.backend.pill.model.IntakeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntakeInfoRepository extends JpaRepository<IntakeInfo, Long> {
}

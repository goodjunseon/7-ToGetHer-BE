package com.together.backend.domain.pill.repository;

import com.together.backend.domain.pill.model.IntakeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntakeInfoRepository extends JpaRepository<IntakeInfo, Long> {
}

package com.together.backend.domain.pill.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "intake_info") // 복용법
/*
현재는 IntakeOption enum을 사용하여 option 필드만 있다면 UserPill 엔티티에 넣는것이 나아보임
 */
public class IntakeInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "intake_info_id")
    private Long intakeInfoId;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private IntakeOption option; // 복용 방법

    @Column(name = "name", length = 20)
    private String name;

    @Column(name = "real_days")
    private Integer realDays;

    @Column(name = "fake_days")
    private Integer fakeDays;

    @Column(name = "break_days")
    private Integer breakDays;

    @Column(name = "is_fake")
    private Boolean isFake;

    @Column(name = "is_break")
    private Boolean isBreak;

    @Column(name = "description")
    private String description;

    @Column(name = "prescription_amount")
    private Integer prescriptionAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

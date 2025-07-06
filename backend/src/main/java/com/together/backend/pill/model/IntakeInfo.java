package com.together.backend.pill.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
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
}

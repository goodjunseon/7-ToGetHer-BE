package com.together.backend.domain.calendar.model.entity;


import java.time.LocalDate;
import java.util.List;

import com.together.backend.domain.pill.model.entity.UserPill;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IntakeRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long intakeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pill_id")
    private UserPill userPill;

    private LocalDate intakeDate;
//    private LocalTime intakeTime;
    private Boolean isTaken;

    @Enumerated(EnumType.STRING)
    private IntakeType type;   // 복용타입 (REAL/FAKE/BREAK)

    @OneToMany(mappedBy = "intakeRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BasicRecord> basicRecords;

}


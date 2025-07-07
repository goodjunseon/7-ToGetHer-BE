package com.together.backend.domain.calendar.model.entity;


import java.time.LocalDate;
import java.time.LocalTime;

import com.together.backend.domain.pill.model.UserPill;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IntakeRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long intakeId;

    @ManyToOne
    @JoinColumn(name = "user_pill_id")
    private UserPill userPill;

    private LocalDate intakeDate;
    private LocalTime intakeTime;
    private Boolean isTaken;
}


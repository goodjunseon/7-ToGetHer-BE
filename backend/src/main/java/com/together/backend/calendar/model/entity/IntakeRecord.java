package com.together.backend.calendar.model.entity;


import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.persistence.*;
import lombok.*;

//import com.together.backend.pill.model.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IntakeRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long intakeId;

//    @ManyToOne
//    @JoinColumn(name = "user_pill_id")
//    private UserPill userPill;

    private LocalDate intakeDate;
    private LocalTime intakeTime;
    private Boolean isTaken;
}


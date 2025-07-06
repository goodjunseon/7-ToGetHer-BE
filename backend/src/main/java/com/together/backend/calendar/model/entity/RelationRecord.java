package com.together.backend.calendar.model.entity;

import com.together.backend.user.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RelationRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long relationId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "partner_id")
    private User partner;

    @Enumerated(EnumType.STRING)
    private CondomUsage hadCondom;

    private Boolean hadSex;

    private LocalDate recordDate;
    private LocalDateTime createdAt;
}



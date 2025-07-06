package com.together.backend.calendar.model.entity;

import com.together.backend.calendar.model.CondomUsage;
import com.together.backend.user.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "relation_record")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RelationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long condomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // partnerId 관계가 있다면, 여기도 User 엔티티로 잡아주면 좋음!
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    private User partner;

    @Enumerated(EnumType.STRING)
    @Column(name = "had_condom", length = 10)
    private CondomUsage hadCondom; // Enum으로 저장

    private Boolean hadSex;

    @Column(name = "record_date")
    private LocalDate recordDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}


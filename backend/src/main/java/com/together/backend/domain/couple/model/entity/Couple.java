package com.together.backend.domain.couple.model.entity;

import com.together.backend.domain.user.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "couple_table")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Couple {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "couple_id")
    private Long coupleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 나

    @Column(name = "partner_user_id")
    private Long partnerUserId; // 상대방 user_id (User 테이블 PK, FK nullable)

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 15)
    private CoupleStatus status;

    @Column(name = "invited_at")
    private LocalDateTime invitedAt;

    @Column(name = "connected_at")
    private LocalDateTime connectedAt;
}

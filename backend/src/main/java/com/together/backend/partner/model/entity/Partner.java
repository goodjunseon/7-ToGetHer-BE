package com.together.backend.partner.model.entity;

import com.together.backend.user.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "partner")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Partner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partner_id")
    private Long partnerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 나

    @Column(name = "partner_user_id")
    private Long partnerUserId; // 상대방 user_id (User 테이블 PK, FK nullable)

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 15)
    private PartnerStatus status;

    @Column(name = "invited_at")
    private LocalDateTime invitedAt;

    @Column(name = "connected_at")
    private LocalDateTime connectedAt;
}

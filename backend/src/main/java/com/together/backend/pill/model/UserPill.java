package com.together.backend.pill.model;

import com.together.backend.global.common.model.BaseEntity;
import com.together.backend.user.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_pill")
public class UserPill extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_pill_id")
    private Long userPillId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "intake_info_id", nullable = false)
    private IntakeInfo intakeInfo;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "current_remain")
    private Integer currentRemain;

    @Column(name = "next_purchase_alert")
    private LocalDate nextPurchaseAlert; // 다음 구매 알림 날짜

}

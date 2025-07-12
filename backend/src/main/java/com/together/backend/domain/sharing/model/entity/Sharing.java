package com.together.backend.domain.sharing.model.entity;

import com.together.backend.domain.couple.model.entity.Couple;
import com.together.backend.domain.user.model.entity.User;
import com.together.backend.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Sharing extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sharingId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "couple_id")
    private Couple couple;

    private String sharedUrl;

    @Column(nullable = false)
    private boolean isShared = false;
    private LocalDateTime sharedAt;

    @Column(nullable = false)
    private boolean isConfirmed = false;
    private LocalDateTime ConfirmedAt;

    public static  Sharing create(User user, Couple couple, String sharedUrl) {
        return new Sharing(
                null, // sharingId는 알아서 생김
                user,
                couple,
                sharedUrl,
                true,
                LocalDateTime.now(),
                false,
                null
        );
    }

    public void updateUrl(String sharedUrl) {
        this.sharedUrl = sharedUrl;
        this.isShared = true;
        this.sharedAt = LocalDateTime.now();
    }

    public void confirm() {
        this.isConfirmed = true;
        this.ConfirmedAt = LocalDateTime.now();
    }

}


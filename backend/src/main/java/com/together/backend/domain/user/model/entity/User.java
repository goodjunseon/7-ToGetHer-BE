package com.together.backend.domain.user.model.entity;

import com.together.backend.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_table")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, length = 100)
    private String socialId;// 소셜 로그인 ID (예: "kakao_1234567890")

    @Column(length = 50)
    private String nickname;

    @Column(unique = true, length = 100)
    private String email;

    @Column
    private String profileImageUrl; // 프로필 이미지 URL

    private Boolean isTakingPill = false; // 약 복용 여부

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Role role;

}
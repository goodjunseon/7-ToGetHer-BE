package com.together.backend.user.model.entity;

import com.together.backend.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Gender gender;

    private LocalDateTime birthYear; // 태어난 연도 : 2000, 2001, ... 형식에 따라 int 또는 String 고려중

    private Boolean smoker; // isSmoker는 Lombok 사용시 isIsSmoker()와 헷갈릴 가능성이 있음

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Role role;
}
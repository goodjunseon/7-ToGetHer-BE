package com.together.backend.global.security.oauth2.dto;

import com.together.backend.user.model.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class UserDTO {
    private String socialId; // "kakao_1234567890"
    private String name; // "홍길동"
    private String email; // "pzs2001926@gmail.com"
    private String profileImageUrl; // "https://example.com/profile.jpg"
    private Role role; // "ROLE_USER" (기본값, 추후 개발 시 추가)

}
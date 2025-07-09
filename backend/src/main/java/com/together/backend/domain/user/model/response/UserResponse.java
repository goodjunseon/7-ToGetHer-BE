package com.together.backend.domain.user.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserResponse {
    private String email; // 유저 이메일
    private String role; // 유저 역할
}

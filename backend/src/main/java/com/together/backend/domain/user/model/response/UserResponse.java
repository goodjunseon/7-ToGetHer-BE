package com.together.backend.domain.user.model.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long userId;
    private String socialId;
    private String nickname;
    private String email;
    private String profileImage;
    private boolean isTakingPill;
    private String role;
}

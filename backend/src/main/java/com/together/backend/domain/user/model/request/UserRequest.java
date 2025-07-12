package com.together.backend.domain.user.model.request;

import lombok.Getter;

@Getter
public class UserRequest {
    public String nickname;
    public boolean isTakingPill;
    public String role; // 유저 역할

}

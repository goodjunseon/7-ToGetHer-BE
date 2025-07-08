package com.together.backend.domain.user.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPageResponse {
    private String nickname;
    private String email;
    private String profileImage;
}

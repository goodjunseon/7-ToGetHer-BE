package com.together.backend.domain.user.model.response.mainpageinfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoResponse {
    private String email;
    private String nickname;
    private String profileImage;
}

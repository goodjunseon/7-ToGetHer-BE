package com.together.backend.domain.user.model.response;

import com.together.backend.domain.user.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MyPageResponse {
    private String nickname;
    private String email;
    private String profileImage;

    public static MyPageResponse from(User user){
        return MyPageResponse.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImage(user.getProfileImageUrl())
                .build();
    }
}

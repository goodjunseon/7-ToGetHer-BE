package com.together.backend.domain.user.model.response;


import com.together.backend.domain.user.model.entity.User;
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
    private Boolean isTakingPill; // 기본형 boolean은 null이 허용이 안되기 때문에 Boolean으로  수정
    private String role;


    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .socialId(user.getSocialId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImage(user.getProfileImageUrl())
                .isTakingPill(user.getIsTakingPill())
                .role(user.getRole().name())
                .build();
    }
}

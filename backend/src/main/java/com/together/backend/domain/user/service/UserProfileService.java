package com.together.backend.domain.user.service;

import com.together.backend.domain.user.model.entity.Role;
import com.together.backend.domain.user.model.entity.User;
import com.together.backend.domain.user.model.request.UserRequest;
import com.together.backend.domain.user.model.response.MyPageResponse;
import com.together.backend.domain.user.model.response.UserResponse;
import com.together.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserRepository userRepository;

    public MyPageResponse getMyPageInfo(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다: "));
        return MyPageResponse.from(user);
    }

    @Transactional
    public void postUserInfo(String email, UserRequest userRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String nickname= userRequest.getNickname();
        String role = userRequest.getRole();

        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임은 비어있을 수 없습니다.");
        }

        if (!role.equals("ROLE_USER") && !role.equals("ROLE_PARTNER")) {
            throw new IllegalArgumentException("잘못된 Role 값입니다.");
        }

        user.setNickname(nickname);
        user.setRole(Role.valueOf(role));
        user.setIsTakingPill(userRequest.isTakingPill());

    }

    public UserResponse getUserInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return UserResponse.from(user);
    }

}

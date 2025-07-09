package com.together.backend.global.security.oauth2;

import com.together.backend.global.security.oauth2.dto.CustomOAuth2User;
import com.together.backend.global.security.oauth2.dto.KakaoResponse;
import com.together.backend.global.security.oauth2.dto.UserDTO;
import com.together.backend.domain.user.model.entity.Role;
import com.together.backend.domain.user.model.entity.User;
import com.together.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() 실행 : OAuth2 로그인 요청 처리 시작");

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.debug("OAuth2User attributes: {}", oAuth2User.getAttributes());

        KakaoResponse kakaoResponse = new KakaoResponse(oAuth2User.getAttributes());
        log.info("카카오에서 추출한 email: {}", kakaoResponse.getEmail());



        User existData = userRepository.findByEmail(kakaoResponse.getEmail()); // 이메일로 사용자 조회

        // 신규 회원일 때
        if (existData == null) {
            User user = new User();
            user.setSocialId(kakaoResponse.getSocialId());
            user.setNickname(kakaoResponse.getName());
            user.setEmail(kakaoResponse.getEmail());
            user.setProfileImageUrl(kakaoResponse.getProfileImageUrl());
            user.setRole(Role.ROLE_USER);
            userRepository.save(user);

            UserDTO userDTO = UserDTO.builder()
                    .socialId(user.getSocialId())
                    .name(user.getNickname())
                    .email(user.getEmail())
                    .profileImageUrl(user.getProfileImageUrl())
                    .role(user.getRole())
                    .build();
            return new CustomOAuth2User(userDTO);
        }
        // 기존 회원일 때
        else {

            // 이메일 또는 닉네임이 변경되었는지 확인
            if (!existData.getNickname().equals(kakaoResponse.getName())||
            !existData.getEmail().equals(kakaoResponse.getEmail()) ||
            !existData.getProfileImageUrl().equals(kakaoResponse.getProfileImageUrl())) {
                log.info("기존 사용자 정보 업데이트: 이메일 또는 닉네임 변경");
                existData.setEmail(kakaoResponse.getEmail());
                existData.setNickname(kakaoResponse.getName());
                existData.setProfileImageUrl(kakaoResponse.getProfileImageUrl());
                userRepository.save(existData); // 변경된 내용 저장
            }

            UserDTO userDTO = UserDTO.builder()
                    .socialId(kakaoResponse.getSocialId())
                    .name(kakaoResponse.getName())
                    .email(kakaoResponse.getEmail())
                    .profileImageUrl(kakaoResponse.getProfileImageUrl())
                    .role(existData.getRole())
                    .build();
            return new CustomOAuth2User(userDTO);
        }
    }
}

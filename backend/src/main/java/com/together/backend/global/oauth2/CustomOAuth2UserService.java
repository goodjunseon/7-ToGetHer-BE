package com.together.backend.global.oauth2;

import com.together.backend.global.oauth2.dto.CustomOAuth2User;
import com.together.backend.global.oauth2.dto.KakaoResponse;
import com.together.backend.global.oauth2.dto.OAuth2Response;
import com.together.backend.global.oauth2.dto.UserDTO;
import com.together.backend.user.entity.Role;
import com.together.backend.user.entity.User;
import com.together.backend.user.repository.UserRepository;
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

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if("kakao".equals(registrationId)) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다: " + registrationId);
        }

        String email = oAuth2Response.getEmail();
        if (email == null) {
            log.warn("이메일이 존재하지 않습니다. attributes: {}", oAuth2User.getAttributes());
            throw new OAuth2AuthenticationException("이메일이 존재하지 않아 사용자 정보를 생성할 수 없습니다.");
        }

        // 리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디 값을 만듬
        String provider = oAuth2Response.getProvider();         // "kakao"
        String providerId = oAuth2Response.getProviderId();     // "1234567890"

        String uniqueUsername = provider + "_" + providerId;    // "kakao_1234567890"
        User existData = userRepository.findBySocialId(uniqueUsername); // socialId로 사용자 조회

        // 신규 회원일 때
        if (existData == null) {
            User user = new User();
            user.setSocialId(uniqueUsername);
            user.setNickname(oAuth2Response.getName());
            user.setEmail(oAuth2Response.getEmail());
            user.setRole(Role.ROLE_USER);
            userRepository.save(user);

            log.info("신규 사용자 등록: {}", email);
            UserDTO userDTO = UserDTO.builder()
                    .socialId(uniqueUsername)
                    .name(user.getNickname())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .build();
            return new CustomOAuth2User(userDTO);
        }
        // 기존 회원일 때
        else {
            log.info("기존 사용자 로그인: {}", email);

            // 이메일 또는 닉네임이 변경되었는지 확인
            if (!existData.getNickname().equals(oAuth2Response.getName())||
            !existData.getEmail().equals(oAuth2Response.getEmail())) {
                log.info("기존 사용자 정보 업데이트: 이메일 또는 닉네임 변경");
                existData.setEmail(oAuth2Response.getEmail());
                existData.setNickname(oAuth2Response.getName());
                userRepository.save(existData); // 변경된 내용 저장
            }

            UserDTO userDTO = UserDTO.builder()
                    .socialId(uniqueUsername)
                    .name(oAuth2Response.getName())
                    .email(oAuth2Response.getEmail())
                    .role(existData.getRole())
                    .build();
            return new CustomOAuth2User(userDTO);
        }
    }
}

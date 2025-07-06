package com.together.backend.user.service;

import com.together.backend.global.security.jwt.service.BlackListTokenService;
import com.together.backend.global.security.jwt.service.JwtTokenService;
import com.together.backend.user.model.entity.User;
import com.together.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final BlackListTokenService blackListTokenService;
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    public void deleteUser(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        log.info("이메일: {}", email);


        if (optionalUser.isEmpty()) {
            log.warn("사용자를 찾을 수 없습니다: {}", email);
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        User user = optionalUser.get();
        userRepository.delete(user);
        log.info("userRepository.delete(user) 호출: {}", user.getEmail());

    }

    public void logout(String accessToken) {
        // 블랙리스트에 토큰 추가
        blackListTokenService.blackListTokenSave(accessToken);
        log.info("blackListTokenService.blackListTokenSave() 호출");

        // 리프레시 토큰 삭제
        jwtTokenService.refreshTokenDelete(accessToken);
        log.info("jwtTokenService.refreshTokenDelete() 호출");
    }

}

package com.together.backend.domain.notification.controller;

import com.together.backend.domain.notification.service.NotificationService;
import com.together.backend.domain.notification.service.NotificationSseService;
import com.together.backend.global.security.oauth2.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationSseController {

    private final NotificationSseService notificationSseService;

    @GetMapping("/subscribe")
    public SseEmitter subscribe(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        if (oAuth2User == null) {
            // 인증되지 않은 요청
            throw new IllegalArgumentException("Unauthorized: 로그인 필요");
        }
        String email = oAuth2User.getEmail();
        System.out.println("SSE subscribe 요청: "+ email);
        return notificationSseService.subscribe(email);
    }
}

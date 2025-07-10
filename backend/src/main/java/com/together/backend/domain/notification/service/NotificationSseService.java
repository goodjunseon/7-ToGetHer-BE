package com.together.backend.domain.notification.service;

import com.together.backend.domain.notification.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSseService {

    // email을 key로 SSE emitter 관리
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 1. SSE 구독 요청
    public SseEmitter subscribe(String email) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1시간 타임아웃
        emitters.put(email, emitter);
        log.info("[SSE] emitter 등록: email={}, 현재 연결 수: {}", email, emitters.size());

        emitter.onCompletion(() -> {
            emitters.remove(email);
            log.info("[SSE] emitter 정상종료: email={}, 연결 수: {}", email, emitters.size());
        });
        emitter.onTimeout(() -> {
            emitters.remove(email);
            log.info("[SSE] emitter 타임아웃: email={}, 연결 수: {}", email, emitters.size());
        });
        emitter.onError(e -> {
            emitters.remove(email);
            log.warn("[SSE] emitter 오류 발생, 제거: email={}, 에러: {}", email, e.toString());
        });

        try {
            emitter.send(SseEmitter.event().name("connect").data("connected!"));
            log.info("[SSE] 연결 확인용 더미 이벤트 전송: email={}", email);
        } catch (IOException e) {
            emitter.completeWithError(e);
            log.error("[SSE] 더미 이벤트 전송 실패: email={}", email, e);
        }

        return emitter;
    }

    // 2. 알림이 저장될 때 호출 (NotificationService에서 사용)
    public void notifyUser(Notification notification) {
        String email = notification.getReceiver().getEmail();
        SseEmitter emitter = emitters.get(email);
        if (emitter != null) {
            log.info("[SSE] 알림 PUSH 시도: email={}, 알림 id={}", email, notification.getId());
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notification));
                log.info("[SSE] 알림 PUSH 성공: email={}, 알림 id={}", email, notification.getId());
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(email);
                log.warn("[SSE] 알림 PUSH 실패 & emitter 제거: email={}, 에러: {}", email, e.toString());
            }
        } else {
            log.info("[SSE] 알림 PUSH 실패: email={} → emitter 없음(미연결 상태)", email);
        }
    }
}

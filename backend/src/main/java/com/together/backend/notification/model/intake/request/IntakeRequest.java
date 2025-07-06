package com.together.backend.notification.model.intake.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class IntakeRequest {

    private boolean isEnabled; // 알림 활성화 여부
    private String intakeTime; // 알림 시간 (HH:mm 형식)
}

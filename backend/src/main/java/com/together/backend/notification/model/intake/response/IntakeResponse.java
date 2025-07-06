package com.together.backend.notification.model.intake.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IntakeResponse {
    private boolean isEnabled; // 알림 활성화 여부
    private String intakeTime; // 알림 시간 (HH:mm 형식)
}

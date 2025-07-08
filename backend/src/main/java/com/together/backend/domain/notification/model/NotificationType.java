package com.together.backend.domain.notification.model;

public enum NotificationType {
    // 알림 타입 정의
     PILL_PURCHASE("pill-purchase"), // 약 구매 알림
    PILL_INTAKE("pill-intake"), // 약 복용 알림
    PARTNER_REQUEST("partner-request"), // 파트너 요청
    EMOTION_UPDATE("emotion-update"); // 감정 업데이트


    private final String apiValue;

    NotificationType(String apiValue) { this.apiValue = apiValue; }

    // FE와 통신 시 사용하는 값 반환
    public String getApiValue() {return apiValue; }

    // api에서 넘어온 문자열을 Enum으로 변환
    public static NotificationType fromApiValue(String apiValue) {
        for (NotificationType type : values()) {
            if (type.apiValue.equalsIgnoreCase(apiValue)) {
                return type;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 알림 타입: " + apiValue);
    }
}

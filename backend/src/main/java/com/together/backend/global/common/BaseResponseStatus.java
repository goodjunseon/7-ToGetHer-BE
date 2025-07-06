package com.together.backend.global.common;

import lombok.Getter;


/*
* API 응답 상태 코드 관리
 */
@Getter
public enum BaseResponseStatus {

    // Filter chain에서 발생되는 에러코드 -> 약속된 에러코드
    FAIL(false, 400, "요청 실패"),
    EXPIRED_TOKEN(false, 500, "만료된 토큰입니다."),
    NOT_FOUND(false, 404, "페이지를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(false, 500, "서버 내부 오류입니다."),

    // 요청에 성공한 경우 (1000번대)
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    // 요청에 실패한 경우 (도메인 기능 별로 나누기)

    // 2000번대 ( 예: 사용자 관련 에러 코드 )

    USER_NOT_FOUND(false, 2000, "사용자를 찾을 수 없습니다."),
    EMPTY_JWT(false, 2001, "JWT가 비어있습니다.");




    // 3000번대


    // 4000번대



    private final boolean isSuccess; // 요청 성공 여부
    private final int code; // 응답 코드
    private final String message; // 응답 메시지

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}

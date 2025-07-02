package com.together.backend.global.common;

import lombok.Getter;


/*
* API 응답 상태 코드 관리
 */
@Getter
public enum BaseResponseStatus {

    // 200번대
    OK(true, 200, "요청에 성공하였습니다."), // HTTP 200 OK 응답
    CREATED(true, 201, "요청에 성공하였습니다."), // HTTP 201 Created 응답
    NO_CONTENT(true, 204, "요청에 성공하였습니다."), // HTTP 204 No Content 응답

    // 300번대
    MOVED_PERMANENTLY(true, 301, "요청에 성공하였습니다."), // HTTP 301 Moved Permanently 응답
    FOUND(true, 302, "요청에 성공하였습니다."), // HTTP 302 Found 응답
    NOT_MODIFIED(true, 304, "요청에 성공하였습니다."), // HTTP 304 Not Modified 응답

    // 400번대
    BAD_REQUEST(false, 400, "잘못된 요청입니다."), // HTTP 400 Bad Request 응답
    UNAUTHORIZED(false, 401, "인증되지 않은 사용자입니다."), // HTTP 401 Unauthorized 응답
    FORBIDDEN(false, 403, "접근이 금지된 사용자입니다."), // HTTP 403 Forbidden 응답

    // 500번대
    INTERNAL_SERVER_ERROR(false, 500, "서버 오류가 발생했습니다."), // HTTP 500 Internal Server Error 응답
    BAD_GATEWAY(false, 502, "잘못된 게이트웨이입니다."), // HTTP 502 Bad Gateway 응답
    SERVICE_UNAVAILABLE(false, 503, "서비스를 사용할 수 없습니다."); // HTTP 503 Service Unavailable 응답


    private final boolean isSuccess; // 요청 성공 여부
    private final int code; // 응답 코드
    private final String message; // 응답 메시지

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}

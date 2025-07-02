package com.together.backend.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class BaseResponse<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess; // true or false
    private final int status; // HTTP 상태 코드
    private final String message; // 응답 메시지
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private  T result;

    // 요청에 성공한 경우 200 OK 통일
    public BaseResponse(T result) {
        this.isSuccess = BaseResponseStatus.OK.isSuccess();
        this.status = BaseResponseStatus.OK.getCode();
        this.message = BaseResponseStatus.OK.getMessage();
        this.result = result;
    }

    // 요청에 실패한 경우
    public BaseResponse(BaseResponseStatus status) {
        this.isSuccess = status.isSuccess();
        this.status = status.getCode();
        this.message = status.getMessage();
    }

    // 요청에 실패 + response 담을 값이 있을 경우
    public BaseResponse(BaseResponseStatus status, T result) {
        this.isSuccess = status.isSuccess();
        this.message = status.getMessage();
        this.status = status.getCode();
        this.result = result;
    }
}

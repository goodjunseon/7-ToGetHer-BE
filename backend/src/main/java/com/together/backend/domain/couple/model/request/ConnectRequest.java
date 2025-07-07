package com.together.backend.domain.couple.model.request;

import lombok.Getter;

@Getter
public class ConnectRequest {
    private String userEmail; // 유저 이메일
    private String partnerEmail; // 파트너 이메일
}

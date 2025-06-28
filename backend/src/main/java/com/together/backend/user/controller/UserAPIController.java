package com.together.backend.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAPIController {
    // 사용자 관련 API 정의
    // 예: 로그인, 사용자 정보 조회, 등

    @GetMapping("/")
    public String index() {
        return "로그인 API";
    }
    @GetMapping("/auth")
    public String auth() {
        return "인증 API";
    }

}
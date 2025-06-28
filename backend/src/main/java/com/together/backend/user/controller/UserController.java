package com.together.backend.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    // 사용자 관련 API 정의
    // 예: 로그인, 사용자 정보 조회, 등


    @GetMapping("/login/kakao")
    public String loginWithKakao() {
        return "카카오 로그인 API";
    }

    @GetMapping("/")
    public String getUsers() {
        return "사용자 목록 조회 API";
    }

    @GetMapping("/{id}")
    public String getUserById() {
        return "사용자 정보 조회 API";
    }

    @PostMapping("/{id}")
    public String updateUser() {
        return "사용자 정보 수정 API";
    }

}
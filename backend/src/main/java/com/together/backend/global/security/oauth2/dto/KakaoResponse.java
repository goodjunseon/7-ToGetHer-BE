package com.together.backend.global.security.oauth2.dto;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class KakaoResponse implements OAuth2Response{

    private final Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) return null;
        Object email = kakaoAccount.get("email");
        return email != null ? email.toString() : null;
    }

    @Override
    public String getName() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        if (properties == null) return null;
        Object nickname = properties.get("nickname");
        return nickname != null ? nickname.toString() : null;
    }
}
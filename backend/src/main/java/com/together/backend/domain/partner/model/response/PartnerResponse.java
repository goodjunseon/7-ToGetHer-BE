package com.together.backend.domain.partner.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PartnerResponse {
    private String name; // 파트너 이름
    private String profileImageUrl; // 프로필 이미지 URL
}

package com.together.backend.domain.user.model.response.mainpageinfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PartnerInfoResponse {
    private String nickname;
    private boolean connected;
    private long daysTogether;
}

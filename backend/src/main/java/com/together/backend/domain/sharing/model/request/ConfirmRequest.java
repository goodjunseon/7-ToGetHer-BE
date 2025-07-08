package com.together.backend.domain.sharing.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConfirmRequest {
    @NotNull
    private Long sharingUserId; // 초대한 사용자 Id
    private String userEmail;
}

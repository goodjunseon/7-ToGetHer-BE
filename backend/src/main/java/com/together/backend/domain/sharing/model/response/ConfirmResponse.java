package com.together.backend.domain.sharing.model.response;

import com.together.backend.domain.sharing.model.entity.Sharing;
import com.together.backend.domain.user.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ConfirmResponse {
    private  boolean isConfirmed;
    private LocalDateTime confirmedAt;

    public static ConfirmResponse from(Sharing sharing) {
        return ConfirmResponse.builder()
                .isConfirmed(sharing.isConfirmed())
                .confirmedAt(sharing.getConfirmedAt())
                .build();
    }
}

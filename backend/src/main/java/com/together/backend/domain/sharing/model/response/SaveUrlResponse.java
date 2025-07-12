package com.together.backend.domain.sharing.model.response;

import com.together.backend.domain.sharing.model.entity.Sharing;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class SaveUrlResponse {
    private String sharedUrl;
    private boolean isShared;
    private LocalDateTime sharedAt;

    public static SaveUrlResponse from(Sharing sharing) {
        return SaveUrlResponse.builder()
                .sharedUrl(sharing.getSharedUrl())
                .isShared(sharing.isShared())
                .isShared(sharing.isShared())
                .build();
    }
}

package com.together.backend.domain.sharing.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SaveUrlResponse {
    private String sharedUrl;
    private boolean isShared;
    private LocalDateTime sharedAt;
}

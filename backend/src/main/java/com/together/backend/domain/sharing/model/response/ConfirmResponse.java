package com.together.backend.domain.sharing.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ConfirmResponse {
    private  boolean isConfirmed;
    private LocalDateTime confirmedAt;
}

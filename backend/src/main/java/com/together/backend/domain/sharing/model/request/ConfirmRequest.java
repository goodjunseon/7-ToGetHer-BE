package com.together.backend.domain.sharing.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConfirmRequest {
    @NotNull
    private String userEmail;
}

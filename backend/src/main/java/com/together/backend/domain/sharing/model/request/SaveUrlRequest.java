package com.together.backend.domain.sharing.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SaveUrlRequest {
    @NotBlank(message = "공유 URL은 필수 값입니다.")
    private String sharedUrl;
}

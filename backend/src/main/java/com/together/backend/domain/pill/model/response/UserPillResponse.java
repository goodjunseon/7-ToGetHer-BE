package com.together.backend.domain.pill.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPillResponse {
    private final String option;
    private final String startDate; // YYYY-MM-DD 형식
}

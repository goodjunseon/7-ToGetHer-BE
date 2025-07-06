package com.together.backend.pill.model.response;

import com.together.backend.pill.model.IntakeOption;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPillResponse {
    private final String option;
    private final String startDate; // YYYY-MM-DD 형식
}

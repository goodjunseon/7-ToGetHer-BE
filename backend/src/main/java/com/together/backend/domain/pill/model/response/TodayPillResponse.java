package com.together.backend.domain.pill.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodayPillResponse {
    private boolean isTaken;
    private Long minutesLeft;
}

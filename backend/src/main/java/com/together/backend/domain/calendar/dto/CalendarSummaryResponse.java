package com.together.backend.domain.calendar.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CalendarSummaryResponse {
    private String date;
    private String moodEmoji;
}

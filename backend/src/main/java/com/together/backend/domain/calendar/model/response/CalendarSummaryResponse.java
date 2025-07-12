package com.together.backend.domain.calendar.model.response;

import com.together.backend.domain.calendar.model.entity.MoodType;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CalendarSummaryResponse {
    private String date;
    private MoodType moodEmoji;
}

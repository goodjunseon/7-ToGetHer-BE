package com.together.backend.domain.calendar.model.response;

import com.together.backend.domain.calendar.model.entity.CondomUsage;
import com.together.backend.domain.calendar.model.entity.MoodType;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CalendarDetailResponse {
    private String date;
    private Boolean takenPill;
    private CondomUsage usedCondom;
    private Boolean hadSex;
    private MoodType moodEmoji;
}

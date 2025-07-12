package com.together.backend.domain.calendar.model.response;

import com.together.backend.domain.calendar.model.entity.CondomUsage;
import com.together.backend.domain.calendar.model.entity.MoodType;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CalendarRecordResponse {
    private String date;
    private Boolean hadSex;
    private Boolean takenPill;
    private CondomUsage usedCondom; // Enum!
    private MoodType moodEmoji;
}


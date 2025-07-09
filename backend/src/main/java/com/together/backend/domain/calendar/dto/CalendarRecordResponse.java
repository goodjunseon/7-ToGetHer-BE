package com.together.backend.domain.calendar.dto;

import com.together.backend.domain.calendar.model.entity.CondomUsage;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CalendarRecordResponse {
    private String date;
    private Boolean hadSex;
    private Boolean takenPill;
    private CondomUsage usedCondom; // Enum!
    private String moodEmoji;
}


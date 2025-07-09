package com.together.backend.domain.calendar.dto;

import com.together.backend.domain.calendar.model.entity.CondomUsage;
import lombok.*;

// 캘린더 기록 등록용 DTO

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CalendarRecordRequest {
    private String date;
    private Boolean hadSex;
    private Boolean takenPill;
    private CondomUsage usedCondom;
    private String moodEmoji;
}

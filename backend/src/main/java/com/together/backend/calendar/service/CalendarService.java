package com.together.backend.calendar.service;

import com.together.backend.calendar.dto.CalendarRecordRequest;
import com.together.backend.calendar.dto.CalendarRecordResponse;
import com.together.backend.user.model.entity.User;
import org.springframework.stereotype.Service;

@Service
public class CalendarService {

    public void saveMaleRecord(User user, CalendarRecordRequest request) {
        // 남성 기록 저장 로직
    }

    public void saveFemaleRecord(User user, CalendarRecordRequest request) {
        // 여성 기록 저장 로직
    }

    public CalendarRecordResponse findRecordByDate(User user, String date) {
        return CalendarRecordResponse.builder()
                .date(date)
                .build();
    }
}

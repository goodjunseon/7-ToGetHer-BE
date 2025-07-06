package com.together.backend.calendar.service;

import com.together.backend.calendar.dto.CalendarRecordRequest;
import com.together.backend.calendar.dto.CalendarRecordResponse;
import com.together.backend.calendar.repository.BasicRecordRepository;
import com.together.backend.calendar.repository.IntakeRecordRepository;
import com.together.backend.calendar.repository.RelationRecordRepository;
import com.together.backend.user.model.entity.User;
import com.together.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CalendarService {
    private final RelationRecordRepository relationRecordRepository;
    private final BasicRecordRepository basicRecordRepository;
    private final IntakeRecordRepository intakeRecordRepository;
    private final UserRepository userRepository;

    // 캘린더 기록 저장
    public void saveRecord(User user, CalendarRecordRequest request) {
        if(request)
    }
}

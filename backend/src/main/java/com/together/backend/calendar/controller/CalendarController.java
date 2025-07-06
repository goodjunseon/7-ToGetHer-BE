package com.together.backend.calendar.controller;

import com.together.backend.calendar.dto.CalendarRecordRequest;
import com.together.backend.calendar.dto.CalendarRecordResponse;
import com.together.backend.calendar.service.CalendarService;
import com.together.backend.global.common.BaseResponse;
import com.together.backend.global.common.BaseResponseStatus;
import com.together.backend.user.model.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/calendar")
public class CalendarController {
    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<List<CalendarRecordResponse>>> saveRecord(
            @RequestBody CalendarRecordRequest request,
            @AuthenticationPrincipal User user
    ) {
        try {
            calendarService.saveCalendarRecord(user, request);
            List<CalendarRecordResponse> records = calendarService.getCalendarList(user);
            return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.OK, records));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR));
        }
    }

}
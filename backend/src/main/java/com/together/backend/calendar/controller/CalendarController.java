package com.together.backend.calendar.controller;

import com.together.backend.calendar.dto.CalendarRecordRequest;
import com.together.backend.calendar.dto.CalendarRecordResponse;
import com.together.backend.calendar.service.CalendarService;
import com.together.backend.global.common.BaseResponse;
import com.together.backend.global.common.BaseResponseStatus;
import com.together.backend.global.security.oauth2.dto.CustomOAuth2User;
import com.together.backend.user.model.entity.User;
import com.together.backend.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {
    private final CalendarService calendarService;
    private final UserRepository userRepository;

    public CalendarController(CalendarService calendarService, UserRepository userRepository) {
        this.calendarService = calendarService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<List<CalendarRecordResponse>>> saveRecord(
            @RequestBody CalendarRecordRequest request,
            @AuthenticationPrincipal CustomOAuth2User customUser
    ) {
        if (customUser == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED));
        }
        // 실제 DB의 User 조회
        String email = customUser.getEmail();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED));
        }
        try {
            calendarService.saveCalendarRecord(user, request);
            return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.OK, null));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR));
        }
    }

}
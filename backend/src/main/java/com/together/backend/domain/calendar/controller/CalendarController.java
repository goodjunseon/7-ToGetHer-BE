package com.together.backend.domain.calendar.controller;

import com.together.backend.domain.calendar.dto.CalendarDetailResponse;
import com.together.backend.domain.calendar.dto.CalendarRecordRequest;
import com.together.backend.domain.calendar.dto.CalendarRecordResponse;
import com.together.backend.domain.calendar.dto.CalendarSummaryResponse;
import com.together.backend.domain.calendar.service.CalendarService;
import com.together.backend.global.common.BaseResponse;
import com.together.backend.global.common.BaseResponseStatus;
import com.together.backend.global.security.oauth2.dto.CustomOAuth2User;
import com.together.backend.domain.user.model.entity.User;
import com.together.backend.domain.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED));
        }
        User user = userOpt.get(); // 또는 userOpt.orElseThrow(() -> ...);

        try {
            calendarService.saveCalendarRecord(user, request);
            return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.OK, null));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<BaseResponse<List<CalendarSummaryResponse>>> getCalendarSummary(
            @RequestParam("month") String month,
            @AuthenticationPrincipal CustomOAuth2User customUser
    ) {
        System.out.println("hello");
        if(customUser == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED));
        }

        // DB의 User 엔티티 조회
        Optional<User> userOpt = userRepository.findByEmail(customUser.getEmail());
        if(userOpt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED));
        }
        User user = userOpt.get();

        try {
            List<CalendarSummaryResponse> result = calendarService.getCalendarSummary(user, month);
            return ResponseEntity.ok(new BaseResponse<>(result));
        } catch (DateTimeException e) {
            return ResponseEntity.badRequest()
                    .body(new BaseResponse<>(BaseResponseStatus.BAD_REQUEST, null));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/detail")
    public ResponseEntity<BaseResponse<CalendarDetailResponse>> getCalendarDetail(
            @RequestParam("date") String dateStr,
            @AuthenticationPrincipal CustomOAuth2User customUser
    ) {
        // 인증 확인
        if (customUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED));
        }

        // 유저 엔티티 조회
        Optional<User> userOpt = userRepository.findByEmail(customUser.getEmail());
        if(userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED));
        }

        User user = userOpt.get();

        try {
            LocalDate date = LocalDate.parse(dateStr);

            CalendarDetailResponse result = calendarService.getCalendarDetail(user, date);
            return ResponseEntity.ok(new BaseResponse<>(result));
        } catch (DateTimeException e) {
            return ResponseEntity.badRequest()
                    .body(new BaseResponse<>(BaseResponseStatus.BAD_REQUEST, null));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
package com.together.backend.domain.calendar.controller;

import com.together.backend.domain.calendar.model.response.CalendarDetailResponse;
import com.together.backend.domain.calendar.model.request.CalendarRecordRequest;
import com.together.backend.domain.calendar.model.response.CalendarRecordResponse;
import com.together.backend.domain.calendar.model.response.CalendarSummaryResponse;
import com.together.backend.domain.calendar.service.CalendarService;
import com.together.backend.global.common.BaseResponse;
import com.together.backend.global.common.BaseResponseStatus;
import com.together.backend.global.security.oauth2.dto.CustomOAuth2User;
import com.together.backend.domain.user.model.entity.User;
import com.together.backend.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
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
        log.info("[API] /api/calendar POST 호출: customUser={}", customUser != null ? customUser.getEmail() : "null");

        if (customUser == null) {
            log.warn("[API] 인증 실패 (customUser==null)");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED));
        }
        String email = customUser.getEmail();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            log.warn("[API] 인증 실패 (userOpt.isEmpty): email={}", email);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED));
        }
        User user = userOpt.get();

        try {
            calendarService.saveCalendarRecord(user, request);
            log.info("[API] /api/calendar POST 성공: userId={}", user.getUserId());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse<>(BaseResponseStatus.OK));
        } catch (Exception e) {
            log.error("[API] /api/calendar POST 실패: userId={}, message={}", user.getUserId(), e.getMessage(), e);
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
        log.info("[API] /api/calendar/summary GET 호출: customUser={}, month={}", customUser != null ? customUser.getEmail() : "null", month);

        if(customUser == null) {
            log.warn("[API] 인증 실패 (customUser==null)");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED));
        }

        Optional<User> userOpt = userRepository.findByEmail(customUser.getEmail());
        if(userOpt.isEmpty()) {
            log.warn("[API] 인증 실패 (userOpt.isEmpty): email={}", customUser.getEmail());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED));
        }
        User user = userOpt.get();

        try {
            List<CalendarSummaryResponse> result = calendarService.getCalendarSummary(user, month);
            log.info("[API] /api/calendar/summary GET 성공: userId={}, count={}", user.getUserId(), result.size());
            return ResponseEntity.ok(new BaseResponse<>(result));
        } catch (DateTimeException e) {
            log.warn("[API] 잘못된 날짜 포맷: month={}, message={}", month, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new BaseResponse<>(BaseResponseStatus.BAD_REQUEST, null));
        } catch (Exception e) {
            log.error("[API] /api/calendar/summary GET 실패: userId={}, message={}", user.getUserId(), e.getMessage(), e);
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
        log.info("[API] /api/calendar/detail GET 호출: customUser={}, date={}", customUser != null ? customUser.getEmail() : "null", dateStr);

        if (customUser == null) {
            log.warn("[API] 인증 실패 (customUser==null)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED));
        }

        Optional<User> userOpt = userRepository.findByEmail(customUser.getEmail());
        if(userOpt.isEmpty()) {
            log.warn("[API] 인증 실패 (userOpt.isEmpty): email={}", customUser.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED));
        }

        User user = userOpt.get();

        try {
            LocalDate date = LocalDate.parse(dateStr);

            CalendarDetailResponse result = calendarService.getCalendarDetail(user, date);
            log.info("[API] /api/calendar/detail GET 성공: userId={}, date={}", user.getUserId(), dateStr);
            return ResponseEntity.ok(new BaseResponse<>(result));
        } catch (DateTimeException e) {
            log.warn("[API] 잘못된 날짜 포맷: date={}, message={}", dateStr, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new BaseResponse<>(BaseResponseStatus.BAD_REQUEST, null));
        } catch (Exception e) {
            log.error("[API] /api/calendar/detail GET 실패: userId={}, date={}, message={}", user.getUserId(), dateStr, e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR));
        }
    }
}

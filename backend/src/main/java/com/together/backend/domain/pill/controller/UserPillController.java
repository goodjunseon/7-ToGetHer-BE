package com.together.backend.domain.pill.controller;

import com.together.backend.domain.pill.model.response.TodayPillResponse;
import com.together.backend.domain.pill.model.response.UserPillRemainResponse;
import com.together.backend.global.common.BaseResponse;
import com.together.backend.global.common.BaseResponseStatus;
import com.together.backend.global.security.oauth2.dto.CustomOAuth2User;
import com.together.backend.domain.pill.model.entity.IntakeOption;
import com.together.backend.domain.pill.model.request.UserPillRequest;
import com.together.backend.domain.pill.model.response.UserPillResponse;
import com.together.backend.domain.pill.service.UserPillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserPillController {

    private final UserPillService userPillService;

    @PostMapping("user-pill/intake-info")
    public BaseResponse<UserPillResponse> createUserPill(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @RequestBody UserPillRequest dto) {
        if (oAuth2User ==null) {
            log.error("@@@@@인증되지 않은 사용자 요청: {}@@@@@", dto);
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED, "@@@@@로그인 정보가 없습니다.@@@@@");
        }

        String email = oAuth2User.getEmail(); // 클라이언트 이메일 추출
        try {
            IntakeOption intakeOption = userPillService.saveUserPill(dto, email);
            log.info("@@@@@[UserPillController]사용자 {}의 약 복용 정보 저장 성공: {}@@@@@", email, intakeOption.getName());
            UserPillResponse response = new UserPillResponse(intakeOption.getName(), dto.getStartDate());
            return new BaseResponse<>(BaseResponseStatus.OK, response);
        } catch (IllegalArgumentException e) {
            log.error("@@@@@[UserPillController]잘못된 요청 데이터: {}, error={}@@@@@", dto, e.getMessage());
            return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST, e.getMessage());
        } catch(Exception e) {
            log.error("@@@@@[UserPillController]서버 오류: {}@@@@@", e.getMessage());
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("user-pill/intake-info")
    public BaseResponse<UserPillResponse> updateUserPill(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @RequestBody UserPillRequest dto) {
        if (oAuth2User == null) {
            log.error("@@@@@[UserPillController]인증되지 않은 사용자 요청: {}@@@@@", dto);
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED);
        }
        String email = oAuth2User.getEmail(); // 클라이언트 이메일 추출
        try {
            IntakeOption intakeNewOption = userPillService.updateUserPill(dto, email);
            UserPillResponse response = new UserPillResponse(dto.getOption(), dto.getStartDate());
            log.info("@@@@@[UserPillController]사용자 {}의 약 복용 정보 업데이트 성공: {}@@@@@", email, intakeNewOption.getName());
            return new BaseResponse<UserPillResponse>(BaseResponseStatus.OK, response);
        } catch(IllegalArgumentException e){
            log.warn("@@@@@[UserPillController] 약 복용 정보 업데이트 실패(입력값): user={}, error={}@@@@@", email, e.getMessage());
          return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("@@@@@[UserPillController] 약 복용 정보 업데이트 실패(서버오류): user={}, error={}@@@@@", email, e.getMessage());
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/user-pill/remain")
    public BaseResponse<UserPillRemainResponse> getCurrentRemain(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        if (oAuth2User == null) {
            log.warn("@@@@@[UserPillController] 잔량 조회: 인증되지 않은 사용자 요청@@@@@");
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED);
        }
        String email = oAuth2User.getEmail();
        try {
            UserPillRemainResponse response = userPillService.getCurrentRemain(email);
            log.info("@@@@@[UserPillController] 잔량 조회 성공@@@@@");
            return new BaseResponse<>(BaseResponseStatus.OK, response);
        } catch(IllegalArgumentException e) {
            log.warn("@@@@@[UserPillController] 잔량 조회 실패(입력값): user={}, error={}@@@@@", email, e.getMessage());
            return new BaseResponse<>(BaseResponseStatus.NOT_FOUND);
        } catch(Exception e) {
            log.error("@@@@@[UserPillController] 잔량 조회 실패(서버오류): user={}, error={}@@@@@", email, e.getMessage());
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user-pill/minutes-left")
    public BaseResponse<TodayPillResponse> getMinutesLeft(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        if (oAuth2User == null) {
            log.warn("@@@@@[UserPillController] minutes-left: 인증되지 않은 사용자 요청@@@@@");
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED);
        }
        String email = oAuth2User.getEmail();
        try {
            TodayPillResponse response = userPillService.getPillTimeLeft(email);
            log.info("@@@@@[UserPillController] minutes-left 조회 성공@@@@@");
            return new BaseResponse<>(BaseResponseStatus.OK, response);
        } catch(IllegalArgumentException e) {
            log.warn("@@@@@[UserPillController] minutes-left 조회 실패@@@@@");
            return new BaseResponse<>(BaseResponseStatus.NOT_FOUND);
        } catch(Exception e) {
            log.error("@@@@@[UserPillController] minutes-left 조회 실패(서버오류)@@@@@");
            return new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

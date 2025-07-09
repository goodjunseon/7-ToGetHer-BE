package com.together.backend.domain.pill.service;

import com.together.backend.domain.calendar.model.entity.IntakeRecord;
import com.together.backend.domain.calendar.repository.BasicRecordRepository;
import com.together.backend.domain.calendar.repository.IntakeRecordRepository;
import com.together.backend.domain.calendar.service.IntakeRecordInitService;
import com.together.backend.domain.pill.model.IntakeInfo;
import com.together.backend.domain.pill.model.IntakeOption;
import com.together.backend.domain.pill.model.UserPill;
import com.together.backend.domain.pill.model.request.UserPillRequest;
import com.together.backend.domain.pill.model.response.UserPillRemainResponse;
import com.together.backend.domain.pill.repository.IntakeInfoRepository;
import com.together.backend.domain.pill.repository.UserPillRepository;
import com.together.backend.domain.user.model.entity.User;
import com.together.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserPillService {

    private final IntakeInfoRepository intakeInfoRepository;
    private final UserPillRepository userPillRepository;
    private final UserRepository userRepository;
    private final IntakeRecordRepository intakeRecordRepository;
    private final BasicRecordRepository basicRecordRepository;
    private final IntakeRecordInitService intakeRecordInitService;

    public IntakeOption saveUserPill(UserPillRequest dto, String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
            // 기존 UserPill 전부 삭제
            List<UserPill> existing = userPillRepository.findAllByUser(user);
            for (UserPill up : existing) {
                intakeRecordRepository.deleteByUserPill(up);
                userPillRepository.delete(up);
            }

            // dto에서 option을 가져와 IntakeOption으로 변환
            IntakeOption option = IntakeOption.valueOf(dto.getOption());

            // IntakeInfo 엔티티 생성 및 저장
            IntakeInfo intakeInfo = intakeInfoRepository.save(
                    IntakeInfo.builder().option(option).build()
            );
            log.info("IntakeInfo 저장 성공: {}", intakeInfo);



            // 시작 날짜 파싱
            LocalDate startDate = LocalDate.parse(dto.getStartDate());

            // UserPill 엔티티 생성 및 저장
            int defaultRemain = option.getRealDays() + option.getFakeDays();

            UserPill userPill = UserPill.builder()
                    .user(user)
                    .intakeInfo(intakeInfo)
                    .startDate(startDate)
                    .currentRemain(defaultRemain)
                    .build();

            userPillRepository.save(userPill);
            log.info("UserPill 저장 성공: {}", userPill);

            // 초기 기록 인스턴스 생성
            intakeRecordInitService.createInitialRecords(startDate, option, userPill);

            return option;

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("잘못된 요청 데이터: " + e.getMessage(), e);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("잘못된 날짜 형식: " + dto.getStartDate(), e);
        }
    }

    public IntakeOption updateUserPill(UserPillRequest dto, String email) {
        // 사용자 조회
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));

        // 기존 UserPill 조회
        UserPill userPill = userPillRepository.findByUser(user).orElseThrow(()-> new IllegalArgumentException("약 복용 정보가 없습니다: " + email));

        // IntakeOption 업데이트
        IntakeOption newOption = IntakeOption.valueOf(dto.getOption());
        IntakeInfo intakeInfo = userPill.getIntakeInfo();
        intakeInfo.setOption(newOption);
        intakeInfoRepository.save(intakeInfo);
        log.info("IntakeInfo 업데이트 성공: {}", intakeInfo);

        // 시작 날짜 업데이트
        try {
            LocalDate newStartDate = LocalDate.parse(dto.getStartDate());
            userPill.setStartDate(newStartDate);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("잘못된 날짜 형식: " + dto.getStartDate(), e);
        }

        // currentRemain 재지정
        int defaultRemain = newOption.getRealDays() + newOption.getFakeDays();
        userPill.setCurrentRemain(defaultRemain);

        userPillRepository.save(userPill);
        log.info("사용자 {}의 약 복용 정보 업데이트 완료: option={}, startDate={}", email, newOption, dto.getStartDate());

        // 1. 기존 IntakeRecord 모두 삭제
        intakeRecordRepository.deleteByUserPill(userPill);

        // 2. 새로운 IntakeRecord 생성
        intakeRecordInitService.createInitialRecords(
                userPill.getStartDate(),
                newOption,
                userPill
        );
        return newOption;
    }

    // 약 잔량 조회
    public UserPillRemainResponse getCurrentRemain(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->  new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
        UserPill userPill = userPillRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("약 복용정보가 없습니다."));

        return new UserPillRemainResponse(userPill.getCurrentRemain());
    }
}

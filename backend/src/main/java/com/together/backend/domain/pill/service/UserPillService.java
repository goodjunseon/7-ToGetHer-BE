package com.together.backend.domain.pill.service;

import com.together.backend.domain.calendar.model.entity.IntakeRecord;
import com.together.backend.domain.calendar.repository.BasicRecordRepository;
import com.together.backend.domain.calendar.repository.IntakeRecordRepository;
import com.together.backend.domain.calendar.service.IntakeRecordInitService;
import com.together.backend.domain.notificationSettings.model.NotificationSettings;
import com.together.backend.domain.notification.model.NotificationType;
import com.together.backend.domain.notificationSettings.repository.NotificationSettingsRepository;
import com.together.backend.domain.pill.model.entity.IntakeInfo;
import com.together.backend.domain.pill.model.entity.IntakeOption;
import com.together.backend.domain.pill.model.entity.UserPill;
import com.together.backend.domain.pill.model.request.UserPillRequest;
import com.together.backend.domain.pill.model.response.TodayPillResponse;
import com.together.backend.domain.pill.model.response.UserPillRemainResponse;
import com.together.backend.domain.pill.repository.IntakeInfoRepository;
import com.together.backend.domain.pill.repository.UserPillRepository;
import com.together.backend.domain.user.model.entity.User;
import com.together.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

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
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final IntakeRecordInitService intakeRecordInitService;

    // 사용자 이메일로 유저 찾는 로직을 메서드화
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("@@@@@[UserPillService] 이메일로 유저 조회 실패: {}@@@@@", email);
                    return new IllegalArgumentException("해당 이메일 사용자를 찾을 수 없습니다: " +email);
                });
    }

    // IntakeOption 변환
    private IntakeOption parseOption(String option) {
        try {
            return IntakeOption.valueOf(option);
        } catch (Exception e) {
            log.warn("@@@@@[UserPillService] IntakeOption 파싱 실패: {}@@@@@", option);
            throw new IllegalArgumentException("지원하지 않는 옵션입니다: " + option);
        }
    }

    // 날짜 파싱 (실패시 상세 로그)
    private LocalDate parseStartDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            log.warn("@@@@@[UserPillService] 날짜 파싱 실패: {}@@@@@", dateStr);
            throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다: " + dateStr);
        }
    }


    public IntakeOption saveUserPill(UserPillRequest dto, String email) {

        User user = getUserByEmail(email);

        // 기존 UserPill 전부 삭제
        List<UserPill> existing = userPillRepository.findAllByUser(user);
        for (UserPill up : existing) {
            intakeRecordRepository.deleteByUserPill(up);
            userPillRepository.delete(up);
            log.info("@@@@@[UserPillService] 기존 UserPill 및 기록 삭제: user={}. userPillID={}@@@@@", email, up.getUserPillId());
        }

        // dto에서 option을 가져와 IntakeOption으로 변환
        IntakeOption option = parseOption(dto.getOption());
        IntakeInfo intakeInfo = intakeInfoRepository.save(IntakeInfo.builder().option(option).build());
        log.info("@@@@@[UserPillService] IntakeInfo 저장: {}@@@@@", intakeInfo);

        // 시작 날짜 파싱
        LocalDate startDate = parseStartDate(dto.getStartDate());

        // UserPill 엔티티 생성 및 저장
        int defaultRemain = option.getRealDays() + option.getFakeDays();
        // 다음 구매 기록일 저장
        int daysBefore = notificationSettingsRepository
                .findByUserAndType(user, NotificationType.PILL_PURCHASE)
                .map(NotificationSettings::getDaysBefore)
                .orElse(5);

        LocalDate nextPurchaseAlert = startDate.plusDays(defaultRemain - daysBefore);


        UserPill userPill = UserPill.builder()
                .user(user)
                .intakeInfo(intakeInfo)
                .startDate(startDate)
                .currentRemain(defaultRemain)
                .nextPurchaseAlert(nextPurchaseAlert)
                .build();

        userPillRepository.save(userPill);
        log.info("@@@@@[UserPillService] UserPill 저장 성공: {}@@@@@", userPill);

        // 초기 기록 인스턴스 생성
        intakeRecordInitService.createInitialRecords(startDate, option, userPill);
        log.info("@@@@@[UserPillService] IntakeRecord 초기화 완료@@@@@");
        return option;
    }

    public IntakeOption updateUserPill(UserPillRequest dto, String email) {
        // 사용자 조회
        User user = getUserByEmail(email);
        // 기존 UserPill 조회
        UserPill userPill = userPillRepository.findByUser(user) .orElseThrow(() -> {
            log.warn("@@@@@[UserPillService] 약 복용 정보 없음: user={}@@@@@", email);
            return new IllegalArgumentException("약 복용 정보가 없습니다: " + email);
        });

        // IntakeOption 업데이트
        IntakeOption newOption = parseOption(dto.getOption());
        IntakeInfo intakeInfo = userPill.getIntakeInfo();
        intakeInfo.setOption(newOption);
        intakeInfoRepository.save(intakeInfo);
        log.info("@@@@@[UserPillService] IntakeInfo 업데이트 성공: {}@@@@@", intakeInfo);

        LocalDate newStartDate = parseStartDate(dto.getStartDate());
        userPill.setStartDate(newStartDate);

        // currentRemain 재지정
        int defaultRemain = newOption.getRealDays() + newOption.getFakeDays();
        userPill.setCurrentRemain(defaultRemain);

        // **nextPurchaseAlert 재계산 추가**
        int daysBefore = notificationSettingsRepository
                .findByUserAndType(user, NotificationType.PILL_PURCHASE)
                .map(NotificationSettings::getDaysBefore)
                .orElse(5); // 기본값 5

        LocalDate nextPurchaseAlert = newStartDate.plusDays(defaultRemain - daysBefore);
        userPill.setNextPurchaseAlert(nextPurchaseAlert);

        userPillRepository.save(userPill);
        log.info("@@@@@사용자 {}의 약 복용 정보 업데이트 완료: option={}, startDate={}@@@@@", email, newOption, dto.getStartDate());

        // 1. 기존 IntakeRecord 모두 삭제
        intakeRecordRepository.deleteByUserPill(userPill);
        log.info("@@@@@[UserPillService] 기존 IntakeRecord 삭제@@@@@");
        // 2. 새로운 IntakeRecord 생성
        intakeRecordInitService.createInitialRecords(
                userPill.getStartDate(),
                newOption,
                userPill
        );
        log.info("@@@@@[UserPillService] 새로운 IntakeRecord 생성@@@@@");
        return newOption;
    }

    // 약 잔량 조회
    public UserPillRemainResponse getCurrentRemain(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
            UserPill userPill = userPillRepository.findByUser(user)
                    .orElseThrow(() -> {
                        log.warn("@@@@@[UserPillService] 잔량 조회: 약 복용 정보 없음: user={}@@@@@", email);
                        return new IllegalArgumentException("약 복용 정보가 없습니다: " + email);
                    });

            log.info("@@@@@[UserPillService] 잔량 조회 성공: user={}, remain={}@@@@@", email, userPill.getCurrentRemain());
            return new UserPillRemainResponse(userPill.getCurrentRemain());
        } catch (Exception e) {
            log.error("@@@@@[UserPillService] 잔량 조회 중 예외: user={}, error={}@@@@@", email, e.getMessage());
            throw e;
        }
    }

    // 다음 복용까지 남은 시간 조회
    public TodayPillResponse getPillTimeLeft(String email) {
        try {
            User user = getUserByEmail(email);

            UserPill userPill = userPillRepository.findByUser(user)
                    .orElseThrow(() -> {
                        log.warn("@@@@@[UserPillService] minutes-left: 약 복용 정보 없음: user={}@@@@@", email);
                        return new IllegalArgumentException("약 복용 정보가 없습니다: " + email);
                    });

            LocalDate today = LocalDate.now();
            // 오늘의 복용 기록
            Optional<IntakeRecord> intakeOpt = intakeRecordRepository.findByUserPillAndIntakeDate(userPill, today);

            boolean isTaken = intakeOpt.map(r -> Boolean.TRUE.equals(r.getIsTaken())).orElse(false);
            // 알림 시간 가져오기
            Optional<NotificationSettings> notiOpt = notificationSettingsRepository.findByUserAndType(user, NotificationType.PILL_INTAKE);

            if (notiOpt.isEmpty() || notiOpt.get().getNotificationTime() == null) {
                log.warn("@@@@@[UserPillService] minutes-left: 알림 설정 없음 or 시간 미설정: user={}@@@@@", email);
                return new TodayPillResponse(isTaken, null);
            }

            LocalTime pillTime = notiOpt.get().getNotificationTime();
            LocalDateTime pillDateTime = LocalDateTime.of(today, pillTime);

            // 남은 분 계산
            long minutesLeft = Duration.between(LocalDateTime.now(), pillDateTime).toMinutes();

            // (이미 먹었거나 시간이 지났으면 0)
            if (isTaken || minutesLeft < 0) {
                log.info("@@@@@[UserPillService] minutes-left: 이미 복용 or 시간 지남: user={}, isTaken={}, minutesLeft={}@@@@@", email, isTaken, minutesLeft);
                minutesLeft = 0;
            } else {
                log.info("@@@@@[UserPillService] minutes-left: user={}, isTaken={}, minutesLeft={}@@@@@", email, isTaken, minutesLeft);
            }

            return new TodayPillResponse(isTaken, minutesLeft);
        } catch(Exception e) {
            log.error("@@@@@[UserPIllService] minutes-left 조회 중 예외: user={}, error={}@@@@@", email, e.getMessage());
            throw e;
        }
    }
}

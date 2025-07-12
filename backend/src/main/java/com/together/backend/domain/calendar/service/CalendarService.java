package com.together.backend.domain.calendar.service;

import com.together.backend.domain.calendar.model.response.CalendarDetailResponse;
import com.together.backend.domain.calendar.model.request.CalendarRecordRequest;
import com.together.backend.domain.calendar.model.response.CalendarSummaryResponse;
import com.together.backend.domain.calendar.model.entity.BasicRecord;
import com.together.backend.domain.calendar.model.entity.CondomUsage;
import com.together.backend.domain.calendar.model.entity.IntakeRecord;
import com.together.backend.domain.calendar.model.entity.RelationRecord;
import com.together.backend.domain.calendar.repository.BasicRecordRepository;
import com.together.backend.domain.calendar.repository.IntakeRecordRepository;
import com.together.backend.domain.calendar.repository.RelationRecordRepository;
import com.together.backend.domain.couple.model.entity.Couple;
import com.together.backend.domain.couple.repository.CoupleRepository;
import com.together.backend.domain.notification.model.NotificationType;
import com.together.backend.domain.notification.service.NotificationService;
import com.together.backend.domain.pill.model.entity.UserPill;
import com.together.backend.domain.pill.repository.UserPillRepository;
import com.together.backend.domain.user.model.entity.Role;
import com.together.backend.domain.user.model.entity.User;
import com.together.backend.domain.calendar.model.entity.MoodType;
import com.together.backend.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Slf4j
@Service
public class CalendarService {
    private final RelationRecordRepository relationRecordRepository;
    private final BasicRecordRepository basicRecordRepository;
    private final IntakeRecordRepository intakeRecordRepository;
    private final UserRepository userRepository;
    private final CoupleRepository coupleRepository;
    private final UserPillRepository userPillRepository;
    private final NotificationService notificationService;

    @Autowired
    public CalendarService(
            RelationRecordRepository relationRecordRepository,
            BasicRecordRepository basicRecordRepository,
            IntakeRecordRepository intakeRecordRepository,
            UserRepository userRepository,
            CoupleRepository coupleRepository,
            UserPillRepository userPillRepository,
            NotificationService notificationService
    ) {
        this.relationRecordRepository = relationRecordRepository;
        this.basicRecordRepository = basicRecordRepository;
        this.intakeRecordRepository = intakeRecordRepository;
        this.userRepository = userRepository;
        this.coupleRepository = coupleRepository;
        this.userPillRepository = userPillRepository;
        this.notificationService = notificationService;
    }

    // 캘린더 기록 등록 로직
    @Transactional
    public void saveCalendarRecord(User user, CalendarRecordRequest request) {
        try {
            LocalDate date = LocalDate.parse(request.getDate());
            boolean isFemale = user.getRole() == Role.ROLE_USER;      // USER = 여성(주유저)
            boolean isMale = user.getRole() == Role.ROLE_PARTNER;     // PARTNER = 남성(파트너)

            // 0. 커플 row 조회 (role에 따라 user or partnerUserId로)
            Couple couple;
            User partnerUser;
            if (isFemale) {
                couple = coupleRepository.findByUser_UserId(user.getUserId())
                        .orElseThrow(() -> new RuntimeException("커플 정보 없음"));
                partnerUser = userRepository.findByUserId(couple.getPartnerUserId())
                        .orElseThrow(() -> new RuntimeException("파트너 유저 없음"));
            } else if (isMale) {
                couple = coupleRepository.findByPartnerUserId(user.getUserId())
                        .orElseThrow(() -> new RuntimeException("커플 정보 없음"));
                partnerUser = userRepository.findByUserId(couple.getUser().getUserId())
                        .orElseThrow(() -> new RuntimeException("파트너 유저 없음"));
            } else {
                throw new IllegalArgumentException("지원하지 않는 역할입니다.");
            }


            // 1. 항상 여성=유저, 남성=파트너로 맞추기
            User female = isFemale ? user : partnerUser;
            User male = isFemale ? partnerUser : user;

            // 2. RelationRecord (user: female, partner: male, recordDate)
            RelationRecord relationRecord = relationRecordRepository
                    .findByUserAndPartnerAndRecordDate(female, male, date)
                    .orElse(null);

            boolean isNewRecord = false;
            if (relationRecord == null) {
                relationRecord = RelationRecord.builder()
                        .user(female)
                        .partner(male)
                        .recordDate(date)
                        .createdAt(LocalDateTime.now())
                        .hadSex(request.getHadSex())
                        .hadCondom(isMale ? request.getUsedCondom() : null)
                        .build();
                isNewRecord = true;
            }

            // 3. 역할별 기록 제한 및 저장
            if (isFemale) {
                // [불가] 콘돔 사용여부
                if (request.getUsedCondom() != null) {
                    log.warn("[CalendarService] 여성 유저가 콘돔 사용여부 등록 시도");
                    throw new IllegalArgumentException("여성은 콘돔 사용여부를 등록할 수 없습니다.");
                }
                // [가능] 관계 여부
                if (!isNewRecord && request.getHadSex() != null) {
                    relationRecord.setHadSex(request.getHadSex());
                }
                // [콘돔] 무조건 null (여성은 입력 불가, 이미 builder에서 처리됨)

                // [가능] 복용 및 감정
                UserPill userPill = userPillRepository.findByUser_UserId(user.getUserId())
                        .orElseThrow(() -> new RuntimeException("UserPill 정보 없음"));
                IntakeRecord intakeRecord = intakeRecordRepository
                        .findByUserPillAndIntakeDate(userPill, date)
                        .orElseThrow(() -> {
                            log.warn("[CalendarService] 복용 기록 없음: date={}",date);
                            return new RuntimeException("복용 기록이 없습니다.");
                        });

                if (request.getTakenPill() != null) {
                    // 기존값과 비교
                    Boolean prevTaken = intakeRecord.getIsTaken();
                    Boolean nowTaken = request.getTakenPill();
                    intakeRecord.setIsTaken(nowTaken);

                    int curRemain = userPill.getCurrentRemain() != null ? userPill.getCurrentRemain() : 0;
                    // 복용처리로 true가 된 경우에만 잔량 감소
                    if((prevTaken == null || !prevTaken) && nowTaken) {
                        if(curRemain > 0) {
                            userPill.setCurrentRemain(curRemain - 1);
                            log.info("[CalendarService] 복용 잔량 감소: remain={}", curRemain - 1);
                        }
                    } else if((prevTaken != null && prevTaken) && !nowTaken) {
                        userPill.setCurrentRemain(curRemain + 1);
                        log.info("[CalendarService] 복용 잔량 복구: remain={}", curRemain + 1);
                    }
                    userPillRepository.save(userPill);
                    intakeRecordRepository.save(intakeRecord);
                }

                BasicRecord basicRecord = basicRecordRepository.findByIntakeRecord(intakeRecord).orElse(null);
                if (basicRecord == null) {
                    basicRecord = BasicRecord.builder()
                            .user(user)
                            .intakeRecord(intakeRecord)
                            .occuredAt(date.atStartOfDay())
                            .build();
                }
                if (request.getMoodEmoji() != null) {
                    try {
                        basicRecord.setMoodEmoji(request.getMoodEmoji());
                        log.info("[CalendarService] 감정 기록: userId={}, mood={}", user.getUserId(), request.getMoodEmoji());
                    } catch (IllegalArgumentException e) {
                        log.warn("[CalendarService] 잘못된 감정 코드: {}", request.getMoodEmoji());
                        throw new IllegalArgumentException("지원하지 않는 감정 코드입니다: " + request.getMoodEmoji());
                    }
                }
                basicRecordRepository.save(basicRecord);

                // 알림 서비스 호출 로직 추가
                if(request.getMoodEmoji() != null) {
                    // 파트너에게 알림 전송
                    notificationService.sendNotification(
                            partnerUser.getUserId(),
                            user.getUserId(),
                            NotificationType.EMOTION_UPDATE,
                            "감정이 기록되었어요.",
                            user.getNickname() + " 님이 오늘 감정을 등록했어요! 한 번 확인해보세요."
                    );
                }

            } else if (isMale) {
                // [불가] 감정, 복용 기록
                if (request.getMoodEmoji() != null || request.getTakenPill() != null) {
                    log.warn("[CalendarService] 남성 파트너가 감정/복용 기록 시도: userId={}", user.getUserId());
                    throw new IllegalArgumentException("파트너(남성)는 감정 및 복용 기록을 등록할 수 없습니다.");
                }
                // [처음 생성] 여성 기록 관련 필드는 builder에서 이미 null로 처리함
                // [가능] 관계 여부
                if (!isNewRecord && request.getHadSex() != null) {
                    relationRecord.setHadSex(request.getHadSex());
                }
                // [가능] 콘돔 사용여부
                if (!isNewRecord && request.getUsedCondom() != null) {
                    relationRecord.setHadCondom(request.getUsedCondom());
                }
            }

            // 4. RelationRecord 저장
            relationRecordRepository.save(relationRecord);
            log.info("[CalendarService] RelationRecord 저장 완료: userId={}, partnerUserId={}, date={}", female.getUserId(), male.getUserId(), date);

        } catch (Exception e) {
            System.out.println("[CalendarService][에러] " + e.getMessage());
            log.error("[CalendarService] 에러={}", e.getMessage());
            throw e;
        }
    }



    @Transactional
    public List<CalendarSummaryResponse> getCalendarSummary(User me, String month) {
        log.info("[getCalendarSummary] 호출: userId={}, email={}, role={}, month={}", me.getUserId(), me.getEmail(), me.getRole(), month);
        LocalDate start = LocalDate.parse(month + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate end = start.plusMonths(1).minusDays(1);

        User female;

        if (me.getRole() == Role.ROLE_USER) {
            female = me;
        } else if (me.getRole() == Role.ROLE_PARTNER) {
            Couple coupleEntity = coupleRepository.findByPartnerUserId(me.getUserId())
                    .orElseThrow(() -> {
                        log.error("[getCalendarSummary] 커플 정보 없음: partnerUserId={}", me.getUserId());
                        return new RuntimeException("커플 정보 없음");
                    });
            if (coupleEntity.getUser() == null) {
                log.error("[getCalendarSummary] 커플 row의 user_id가 null");
                throw new RuntimeException("커플 row의 user_id가 null입니다.");
            }
            female = coupleEntity.getUser();
        } else {
            log.warn("[getCalendarSummary] 지원하지 않는 role: userId={}, role={}", me.getUserId(), me.getRole());
            throw new IllegalArgumentException("지원하지 않는 role입니다.");
        }

        List<BasicRecord> records = basicRecordRepository
                .findAllByUserAndOccuredAtBetween(female, start.atStartOfDay(), end.atTime(23, 59, 59));

        log.info("[getCalendarSummary] 조회결과: userId={}, count={}", female.getUserId(), records.size());

        return records.stream()
                .map(record -> CalendarSummaryResponse.builder()
                        .date(record.getOccuredAt().toLocalDate().toString())
                        .moodEmoji(record.getMoodEmoji())
                        .build())
                .collect(Collectors.toList());
    }





    public CalendarDetailResponse getCalendarDetail(User me, LocalDate date) {
        log.info("[getCalendarDetail] 호출: userId={}, email={}, role={}, date={}", me.getUserId(), me.getEmail(), me.getRole(), date);
        User female, male;

        if (me.getRole() == Role.ROLE_USER) {
            Couple coupleEntity = coupleRepository.findByUser_UserId(me.getUserId())
                    .orElseThrow(() -> {
                        log.error("[getCalendarDetail] 커플 정보 없음: userId={}", me.getUserId());
                        return new RuntimeException("커플 정보 없음");
                    });
            User partner = userRepository.findByUserId(coupleEntity.getPartnerUserId())
                    .orElseThrow(() -> {
                        log.error("[getCalendarDetail] 파트너 유저 없음: partnerUserId={}", coupleEntity.getPartnerUserId());
                        return new RuntimeException("파트너 유저 없음");
                    });
            female = me;
            male = partner;
        } else if (me.getRole() == Role.ROLE_PARTNER) {
            Couple coupleEntity = coupleRepository.findByPartnerUserId(me.getUserId())
                    .orElseThrow(() -> {
                        log.error("[getCalendarDetail] 커플 정보 없음: partnerUserId={}", me.getUserId());
                        return new RuntimeException("커플 정보 없음");
                    });
            User partner = coupleEntity.getUser();
            if (partner == null) {
                log.error("[getCalendarDetail] 커플 row의 user_id가 null");
                throw new RuntimeException("커플 row의 user_id가 null입니다.");
            }
            female = partner;
            male = me;
        } else {
            log.warn("[getCalendarDetail] 지원하지 않는 role: userId={}, role={}", me.getUserId(), me.getRole());
            throw new IllegalArgumentException("지원하지 않는 role입니다.");
        }

        // 복용 기록
        Optional<UserPill> userPillOpt = userPillRepository.findByUser_UserId(female.getUserId());
        Boolean takenPill = null;

        if(userPillOpt.isPresent())  {
            Optional<IntakeRecord> intakeOpt =
                    intakeRecordRepository.findByUserPillAndIntakeDate(userPillOpt.get(), date);
            takenPill = intakeOpt.map(IntakeRecord::getIsTaken).orElse(null);
        }
        // 감정 기록
        MoodType moodEmoji = basicRecordRepository
                .findAllByUserAndOccuredAtBetween(female, date.atStartOfDay(), date.atTime(23, 59, 59))
                .stream()
                .findFirst()
                .map(BasicRecord::getMoodEmoji)
                .orElse(null);

        // 관계 기록
        Optional<RelationRecord> relationOpt
                = relationRecordRepository.findByUserAndPartnerAndRecordDate(female, male, date);
        Boolean hadSex = relationOpt.map(RelationRecord::getHadSex).orElse(null);
        CondomUsage usedCondom = relationOpt.map(RelationRecord::getHadCondom).orElse(null);

        log.info("[getCalendarDetail] 결과: femaleUserId={}, maleUserId={}, date={}, takenPill={}, mood={}, hadSex={}, usedCondom={}",
                female.getUserId(), male.getUserId(), date, takenPill, moodEmoji, hadSex, usedCondom);

        return CalendarDetailResponse.builder()
                .date(date.toString())
                .takenPill(takenPill)
                .hadSex(hadSex)
                .usedCondom(usedCondom)
                .moodEmoji(moodEmoji)
                .build();
    }
}


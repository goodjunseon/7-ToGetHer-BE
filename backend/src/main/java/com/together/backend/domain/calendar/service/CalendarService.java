package com.together.backend.domain.calendar.service;

import com.together.backend.domain.calendar.dto.CalendarRecordRequest;
import com.together.backend.domain.calendar.dto.CalendarSummaryResponse;
import com.together.backend.domain.calendar.model.entity.BasicRecord;
import com.together.backend.domain.calendar.model.entity.IntakeRecord;
import com.together.backend.domain.calendar.model.entity.RelationRecord;
import com.together.backend.domain.calendar.repository.BasicRecordRepository;
import com.together.backend.domain.calendar.repository.IntakeRecordRepository;
import com.together.backend.domain.calendar.repository.RelationRecordRepository;
import com.together.backend.domain.partner.model.entity.Partner;
import com.together.backend.domain.partner.repository.PartnerRepository;
import com.together.backend.domain.pill.model.UserPill;
import com.together.backend.domain.pill.repository.UserPillRepository;
import com.together.backend.domain.user.model.entity.User;
import com.together.backend.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CalendarService {
    private final RelationRecordRepository relationRecordRepository;
    private final BasicRecordRepository basicRecordRepository;
    private final IntakeRecordRepository intakeRecordRepository;
    private final UserRepository userRepository;
    private final PartnerRepository partnerRepository;
    private final UserPillRepository userPillRepository;

    @Autowired
    public CalendarService(
            RelationRecordRepository relationRecordRepository,
            BasicRecordRepository basicRecordRepository,
            IntakeRecordRepository intakeRecordRepository,
            UserRepository userRepository,
            PartnerRepository partnerRepository,
            UserPillRepository userPillRepository
    ) {
        this.relationRecordRepository = relationRecordRepository;
        this.basicRecordRepository = basicRecordRepository;
        this.intakeRecordRepository = intakeRecordRepository;
        this.userRepository = userRepository;
        this.partnerRepository = partnerRepository;
        this.userPillRepository = userPillRepository;
    }

    // 캘린더 기록 등록 로직
    @Transactional
    public void saveCalendarRecord(User user, CalendarRecordRequest request) {
        try {
            System.out.println("[캘린더 기록] user=" + user);

            LocalDate date = LocalDate.parse(request.getDate());
            System.out.println("[캘린더 기록] 요청 날짜: " + date);

            // 1. UserPill 조회
            UserPill userPill = userPillRepository.findByUser_UserId(user.getUserId())
                    .orElseThrow(() -> {
                        System.out.println("[에러] UserPill 없음 for userId=" + user.getUserId());
                        return new RuntimeException("유저의 UserPill 정보 없음");
                    });
            System.out.println("[캘린더 기록] userPill=" + userPill);

            // 2. 복용 기록
            IntakeRecord intakeRecord = intakeRecordRepository
                    .findByUserPillAndIntakeDate(userPill, date)
                    .orElseThrow(() -> {
                        System.out.println("[에러] IntakeRecord 없음 for userPill=" + userPill + ", date=" + date);
                        return new RuntimeException("복용 기록 없음");
                    });
            System.out.println("[캘린더 기록] intakeRecord=" + intakeRecord);

            if (request.getTakenPill() != null) {
                intakeRecord.setIsTaken(request.getTakenPill());
                intakeRecordRepository.save(intakeRecord);
                System.out.println("[캘린더 기록] 복용 여부(isTaken) 업데이트: " + request.getTakenPill());
            }

            // 3. BasicRecord 조회/생성
            BasicRecord basicRecord = basicRecordRepository
                    .findByIntakeRecord(intakeRecord)
                    .orElse(null);
            System.out.println("[캘린더 기록] basicRecord 조회: " + basicRecord);

            if (basicRecord == null) {
                basicRecord = BasicRecord.builder()
                        .user(user)
                        .intakeRecord(intakeRecord)
                        .occuredAt(date.atStartOfDay())
                        .moodEmoji(request.getMoodEmoji())
                        .build();
                System.out.println("[캘린더 기록] basicRecord 새로 생성: " + basicRecord);
            } else {
                basicRecord.setMoodEmoji(request.getMoodEmoji());
                System.out.println("[캘린더 기록] basicRecord moodEmoji 업데이트: " + request.getMoodEmoji());
            }
            basicRecordRepository.save(basicRecord);

            // 4. 파트너 정보
            Partner partner = partnerRepository.findByUser_UserId(user.getUserId())
                    .orElseThrow(() -> {
                        System.out.println("[에러] Partner 정보 없음 for userId=" + user.getUserId());
                        return new RuntimeException("파트너 정보 없음");
                    });

            Long partnerUserId = partner.getPartnerUserId();
            Optional<User> partnerUserOpt = userRepository.findByUserId(partnerUserId);
            User partnerUser = partnerUserOpt.orElseThrow(() -> new RuntimeException("파트너 유저 없음"));
            System.out.println("[캘린더 기록] partnerUser=" + partnerUser);

            // 5. RelationRecord 조회/생성
            RelationRecord relationRecord = relationRecordRepository
                    .findByUserAndPartnerAndRecordDate(user, partnerUser, date)
                    .orElse(null);
            System.out.println("[캘린더 기록] relationRecord 조회: " + relationRecord);

            if (relationRecord == null) {
                relationRecord = RelationRecord.builder()
                        .user(user)
                        .partner(partnerUser)
                        .recordDate(date)
                        .createdAt(LocalDateTime.now())
                        .build();
                System.out.println("[캘린더 기록] relationRecord 새로 생성: " + relationRecord);
            }
            if (request.getHadSex() != null) {
                relationRecord.setHadSex(request.getHadSex());
                System.out.println("[캘린더 기록] relationRecord hadSex 업데이트: " + request.getHadSex());
            }
            if (request.getUsedCondom() != null) {
                relationRecord.setHadCondom(request.getUsedCondom());
                System.out.println("[캘린더 기록] relationRecord hadCondom 업데이트: " + request.getUsedCondom());
            }

            relationRecordRepository.save(relationRecord);
            System.out.println("[캘린더 기록] relationRecord 저장 완료");

        } catch (Exception e) {
            System.out.println("[캘린더 기록][에러] " + e.getMessage());
            e.printStackTrace();
            throw e; // (실제로는 커스텀 예외 처리)
        }
    }


    // 캘린더 랜딩 시 로직
    public List<CalendarSummaryResponse> getCalendarSummary(User user, String month) {
        // month: "2025-07"
        LocalDate start = LocalDate.parse(month + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate end = start.plusMonths(1).minusDays(1);

        // DB에서 해당 user + 기간 내의 BasicRecord 전부 조회
        List<BasicRecord> records = basicRecordRepository
                .findAllByUserAndOccuredAtBetween(user, start.atStartOfDay(), end.atTime(23, 59, 59));

        return records.stream()
                .map(record -> CalendarSummaryResponse.builder()
                        .date(record.getOccuredAt().toLocalDate().toString())
                        .moodEmoji(record.getMoodEmoji())
                        .build())
                .collect(Collectors.toList());

    }
}


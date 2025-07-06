package com.together.backend.calendar.service;

import com.together.backend.calendar.dto.CalendarRecordRequest;
import com.together.backend.calendar.dto.CalendarRecordResponse;
import com.together.backend.calendar.model.entity.BasicRecord;
import com.together.backend.calendar.model.entity.IntakeRecord;
import com.together.backend.calendar.model.entity.RelationRecord;
import com.together.backend.calendar.repository.BasicRecordRepository;
import com.together.backend.calendar.repository.IntakeRecordRepository;
import com.together.backend.calendar.repository.RelationRecordRepository;
import com.together.backend.partner.model.entity.Partner;
import com.together.backend.partner.repository.PartnerRepository;
import com.together.backend.user.model.entity.User;
import com.together.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class CalendarService {
    private final RelationRecordRepository relationRecordRepository;
    private final BasicRecordRepository basicRecordRepository;
    private final IntakeRecordRepository intakeRecordRepository;
    private final UserRepository userRepository;
    private final PartnerRepository partnerRepository;

    @Autowired
    public CalendarService(
            RelationRecordRepository relationRecordRepository,
            BasicRecordRepository basicRecordRepository,
            IntakeRecordRepository intakeRecordRepository,
            UserRepository userRepository,
            PartnerRepository partnerRepository
    ) {
        this.relationRecordRepository = relationRecordRepository;
        this.basicRecordRepository = basicRecordRepository;
        this.intakeRecordRepository = intakeRecordRepository;
        this.userRepository = userRepository;
        this.partnerRepository = partnerRepository;
    }

    // 캘린더 기록 저장
    @Transactional
    public void saveCalendarRecord(User user, CalendarRecordRequest request) {
        LocalDate date = LocalDate.parse(request.getDate());
        String email = user.getEmail();
        Long userId = user.getUserId();

        // 파트너 조회
        Partner partner = partnerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("파트너 정보 없음"));
        Long partnerUserId = partner.getPartnerUserId();
        User partnerUser = userRepository.findByUserId(partnerUserId);
        // 1. 복용 기록
        IntakeRecord intakeRecord = intakeRecordRepository
                .findByUserEmailAndIntake(email, date)
                .orElseThrow(() -> new RuntimeException("복용 기록 없음"));
        if (request.getTakenPill() != null) {
            intakeRecord.setIsTaken(request.getTakenPill());
            intakeRecordRepository.save(intakeRecord);
        }

        // 2. 복용 기록 기준으로 BasicRecord 조회
        BasicRecord basicRecord = basicRecordRepository
                .findByIntakeRecord(intakeRecord)
                .orElse(null);

        if(basicRecord == null) {
            basicRecord = BasicRecord.builder()
                    .user(user)
                    .intakeRecord(intakeRecord)
                    .occuredAt(date.atStartOfDay())
                    .moodEmoji(request.getMoodEmoji())
                    .build();
        } else {
            basicRecord.setMoodEmoji(request.getMoodEmoji());
        }
        basicRecordRepository.save(basicRecord);

        // 3. 관계 기록
        RelationRecord relationRecord = relationRecordRepository
                .findByUserAndPartnerAndRecordDate(user, partnerUser, date)
                .orElse(null);

        if(relationRecord == null) {
            relationRecord = RelationRecord.builder()
                    .user(user)
                    .partner(partnerUser)
                    .recordDate(date)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
        if(request.getHadSex() != null) relationRecord.setHadSex(request.getHadSex());
        if(request.getUsedCondom() != null) relationRecord.setHadCondom(request.getUsedCondom());

        relationRecordRepository.save(relationRecord);


    }
}

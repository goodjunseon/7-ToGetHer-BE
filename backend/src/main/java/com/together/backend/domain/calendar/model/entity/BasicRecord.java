package com.together.backend.domain.calendar.model.entity;

import jakarta.persistence.Column;
import java.time.LocalDateTime;
import com.together.backend.domain.user.model.entity.User;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "basic_record")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BasicRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long basicRecordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "mood_emoji", length = 10)
    private MoodType moodEmoji;

    @Column(name = "occured_at")
    private LocalDateTime occuredAt;

    // 관계: 복용 기록
    @ManyToOne
    @JoinColumn(name = "intake_id")
    private IntakeRecord intakeRecord;
}


package com.together.backend.calendar.model.entity;

import jakarta.persistence.Column;
import java.time.LocalDateTime;
import com.together.backend.user.model.entity.User;
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

    @Column(name = "mood_emoji", length = 10)
    private String moodEmoji;

    @Column(name = "occured_at")
    private LocalDateTime occuredAt;
}


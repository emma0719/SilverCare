package com.silvercare.silvercarebackend.domain;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "medication_reminders",
        indexes = {
                @Index(name = "idx_reminder_recipient_active", columnList = "care_recipient_id, active")
        }
)
public class Reminder {
    public enum RepeatType {
        DAILY, WEEKLY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // relate to care recipient
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "care_recipient_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_reminder_recipient")
    )
    private CareRecipient careRecipient;

    @Column(name = "med_title", length = 100, nullable = false)
    private String medTitle;

    @Column(name = "dosage_text", length = 100)
    private String dosageText;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_type", length = 10, nullable = false)
    private RepeatType repeatType;

    @Column(name = "days_of_week_bits")
    private Integer daysOfWeekBits;



    @Column(name = "time_points", columnDefinition = "json", nullable = false)
    private String timePoints;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}

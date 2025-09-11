package com.silvercare.silvercarebackend.dto;

import com.silvercare.silvercarebackend.domain.CareRecipient;
import com.silvercare.silvercarebackend.domain.Reminder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReminderRequest {
    private Long careRecipientId;
    private String medTitle;
    private String dosageText;
    private Reminder.RepeatType repeatType;
    private Integer daysOfWeek;
    private String timePoints;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active;

    public Reminder toEntity(CareRecipient careRecipient) {
        return Reminder.builder()
                .careRecipient(careRecipient)
                .medTitle(medTitle)
                .dosageText(dosageText)
                .repeatType(repeatType)

                .timePoints(timePoints)
                .startDate(startDate)
                .endDate(endDate)
                .active(active)
                .build();
    }
}

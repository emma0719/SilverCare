package com.silvercare.silvercarebackend.dto;

import com.silvercare.silvercarebackend.domain.Reminder;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReminderResponse {
    private Long id;
    private Long careRecipientId;
    private String medTitle;
    private String dosageText;
    private Reminder.RepeatType repeatType;
    private Integer daysOfWeek;
    private String timePoints;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active;

    public static ReminderResponse fromEntity(Reminder reminder) {
        return ReminderResponse.builder()
                .id(reminder.getId())
                .careRecipientId(reminder.getCareRecipient().getId())
                .medTitle(reminder.getMedTitle())
                .dosageText(reminder.getDosageText())
                .repeatType(reminder.getRepeatType())

                .timePoints(reminder.getTimePoints())
                .startDate(reminder.getStartDate())
                .endDate(reminder.getEndDate())
                .active(reminder.getActive())
                .build();
    }
}

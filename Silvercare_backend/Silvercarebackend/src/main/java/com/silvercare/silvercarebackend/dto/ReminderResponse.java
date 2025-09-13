package com.silvercare.silvercarebackend.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.silvercare.silvercarebackend.domain.Reminder;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Data
@Builder
public class ReminderResponse {
    private Long id;
    private Long careRecipientId;
    private String medTitle;
    private String dosageText;
    private Reminder.RepeatType repeatType;
    private Integer daysOfWeekBits;
    private List<String> timePoints;  // ✅ 改成 List
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active;

    public static ReminderResponse fromEntity(Reminder reminder) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<String> parsedTimePoints = reminder.getTimePoints() != null
                    ? mapper.readValue(reminder.getTimePoints(), new TypeReference<List<String>>() {})
                    : Collections.emptyList();

            return ReminderResponse.builder()
                    .id(reminder.getId())
                    .careRecipientId(reminder.getCareRecipient().getId())
                    .medTitle(reminder.getMedTitle())
                    .dosageText(reminder.getDosageText())
                    .repeatType(reminder.getRepeatType())
                    .daysOfWeekBits(reminder.getDaysOfWeekBits())
                    .timePoints(parsedTimePoints) // ✅ 返回数组
                    .startDate(reminder.getStartDate())
                    .endDate(reminder.getEndDate())
                    .active(reminder.getActive())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse timePoints JSON", e);
        }
    }
}

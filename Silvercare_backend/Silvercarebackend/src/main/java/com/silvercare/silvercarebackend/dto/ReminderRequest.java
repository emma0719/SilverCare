package com.silvercare.silvercarebackend.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silvercare.silvercarebackend.domain.CareRecipient;
import com.silvercare.silvercarebackend.domain.Reminder;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReminderRequest {

    private Long careRecipientId;
    private String medTitle;
    private String dosageText;
    private Reminder.RepeatType repeatType;
    private Integer daysOfWeekBits;   // ✅ 保持与 Entity 一致
    private List<String> timePoints;  // 前端传数组
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active;

    public Reminder toEntity(CareRecipient careRecipient) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return Reminder.builder()
                    .careRecipient(careRecipient)
                    .medTitle(medTitle)
                    .dosageText(dosageText)
                    .repeatType(repeatType)
                    .daysOfWeekBits(daysOfWeekBits)
                    .timePoints(mapper.writeValueAsString(timePoints)) // ✅ 转成 JSON 字符串
                    .startDate(startDate)
                    .endDate(endDate)
                    .active(active)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert timePoints", e);
        }
    }
}

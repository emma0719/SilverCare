package com.silvercare.silvercarebackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VitalSignDTO {
    private Long id;

    // 基础四大体征
    private Double temperature;
    private Integer heartRate;
    private Integer respiratoryRate;
    private Integer systolicBp;
    private Integer diastolicBp;

    // 扩展体征
    private Integer spo2;
    private Double weight;
    private Double bloodGlucose;
    private Integer painLevel;

    // 只返回 ID，避免实体递归
    private Long careRecipientId;
    private Long recordedByUserId;

    // 统一 ISO-8601（带时区）
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssxxx")
    private OffsetDateTime recordedAt;
}

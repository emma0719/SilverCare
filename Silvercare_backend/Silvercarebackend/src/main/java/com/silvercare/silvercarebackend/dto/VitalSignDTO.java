package com.silvercare.silvercarebackend.dto;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VitalSignDTO {
    private Long id;
    private Double temperature;
    private Integer heartRate;
    private Integer respiratoryRate;
    private Integer systolicBp;
    private Integer diastolicBp;
    private Integer spo2;
    private Double weight;
    private Double bloodGlucose;
    private Integer painLevel;

    private Long careRecipientId; // 只返回ID
    private Long recordedByUserId; // 只返回ID

    private OffsetDateTime recordedAt;
}

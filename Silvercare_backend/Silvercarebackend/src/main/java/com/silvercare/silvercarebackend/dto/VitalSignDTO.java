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
    private Double temperature;
    private Integer heartRate;
    private Integer respiratoryRate;
    private Integer systolicBp;
    private Integer diastolicBp;
    private Integer spo2;
    private Double weight;
    private Double bloodGlucose;
    private Integer painLevel;
    private Long careRecipientId;
    private Long recordedByUserId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssxxx")
    private OffsetDateTime recordedAt;
}

package com.silvercare.silvercarebackend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "vital_signs")
public class VitalSign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 基础四大体征
    @Column(name = "temperature")
    private Double temperature; // 摄氏度

    @Column(name = "heart_rate")
    private Integer heartRate; // bpm

    @Column(name = "respiratory_rate")
    private Integer respiratoryRate; // 次/分钟

    @Column(name = "systolic_bp")
    private Integer systolicBp; // 收缩压

    @Column(name = "diastolic_bp")
    private Integer diastolicBp; // 舒张压

    // 扩展体征
    @Column(name = "spo2")
    private Integer spo2; // %

    @Column(name = "weight")
    private Double weight; // kg

    @Column(name = "blood_glucose")
    private Double bloodGlucose; // mmol/L

    @Column(name = "pain_level")
    private Integer painLevel; // 0–10

    // 关联到受照顾者
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "care_recipient_id", nullable = false)
    private CareRecipient careRecipient;

    // 记录是谁录入
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_user_id")
    private User recordedBy;

    @CreationTimestamp
    @Column(name = "recorded_at", updatable = false)
    private OffsetDateTime recordedAt;
}

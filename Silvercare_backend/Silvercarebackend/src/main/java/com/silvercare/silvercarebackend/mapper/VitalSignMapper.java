package com.silvercare.silvercarebackend.mapper;

import com.silvercare.silvercarebackend.domain.VitalSign;
import com.silvercare.silvercarebackend.dto.VitalSignDTO;

public class VitalSignMapper {

    public static VitalSignDTO toDTO(VitalSign entity) {
        if (entity == null) return null;

        return VitalSignDTO.builder()
                .id(entity.getId())
                .temperature(entity.getTemperature())
                .heartRate(entity.getHeartRate())
                .respiratoryRate(entity.getRespiratoryRate())
                .systolicBp(entity.getSystolicBp())
                .diastolicBp(entity.getDiastolicBp())
                .spo2(entity.getSpo2())
                .weight(entity.getWeight())
                .bloodGlucose(entity.getBloodGlucose())
                .painLevel(entity.getPainLevel())
                .careRecipientId(entity.getCareRecipient() != null ? entity.getCareRecipient().getId() : null)
                .recordedByUserId(entity.getRecordedBy() != null ? entity.getRecordedBy().getId() : null)
                .recordedAt(entity.getRecordedAt())
                .build();
    }

    public static VitalSign toEntity(VitalSignDTO dto) {
        if (dto == null) return null;

        VitalSign entity = new VitalSign();
        entity.setId(dto.getId()); // create 场景可为 null；update 场景可带 id
        entity.setTemperature(dto.getTemperature());
        entity.setHeartRate(dto.getHeartRate());
        entity.setRespiratoryRate(dto.getRespiratoryRate());
        entity.setSystolicBp(dto.getSystolicBp());
        entity.setDiastolicBp(dto.getDiastolicBp());
        entity.setSpo2(dto.getSpo2());
        entity.setWeight(dto.getWeight());
        entity.setBloodGlucose(dto.getBloodGlucose());
        entity.setPainLevel(dto.getPainLevel());
        // careRecipient / recordedBy 交给 Service 通过仓库加载后 set
        return entity;
    }
}

package com.silvercare.silvercarebackend.web;

import com.silvercare.silvercarebackend.domain.VitalSign;
import com.silvercare.silvercarebackend.dto.VitalSignDTO;
import com.silvercare.silvercarebackend.mapper.VitalSignMapper;
import com.silvercare.silvercarebackend.service.VitalSignService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/vital-signs", produces = "application/json")
@CrossOrigin(origins = "http://localhost:8082")
public class VitalSignController {

    private final VitalSignService vitalSignService;

    @PostMapping
    public VitalSignDTO createVitalSign(@RequestBody VitalSignDTO dto) {
        VitalSign entity = VitalSignMapper.toEntity(dto);
        VitalSign saved = vitalSignService.save(entity, dto.getCareRecipientId(), dto.getRecordedByUserId());
        return VitalSignMapper.toDTO(saved);
    }

    @GetMapping("/{recipientId}")
    public List<VitalSignDTO> getVitalSigns(@PathVariable Long recipientId) {
        return vitalSignService.getByCareRecipient(recipientId)
                .stream()
                .map(VitalSignMapper::toDTO)
                .toList();
    }



}

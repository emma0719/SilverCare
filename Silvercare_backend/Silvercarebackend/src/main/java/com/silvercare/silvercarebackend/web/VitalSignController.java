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
@CrossOrigin(
        origins = {"http://localhost:8080","http://localhost:8082","http://localhost:8083"},
        allowCredentials = "true",
        allowedHeaders = {"Authorization","Content-Type","Accept","Accept-Language","Cache-Control","Pragma"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
public class VitalSignController {

    private final VitalSignService vitalSignService;

    @PostMapping
    public VitalSignDTO createVitalSign(@RequestBody VitalSignDTO dto) {
        VitalSign entity = VitalSignMapper.toEntity(dto);
        VitalSign saved = vitalSignService.save(entity, dto.getCareRecipientId(), dto.getRecordedByUserId());
        return VitalSignMapper.toDTO(saved);
    }

    // GET /api/vital-signs/{recipientId}?days=7
    @GetMapping("/{recipientId}")
    public List<VitalSignDTO> getVitalSigns(
            @PathVariable Long recipientId,
            @RequestParam(defaultValue = "7") int days
    ) {
        return vitalSignService.getByCareRecipient(recipientId, days)
                .stream()
                .map(VitalSignMapper::toDTO)
                .toList();
    }
}

package com.silvercare.silvercarebackend.web;

import com.silvercare.silvercarebackend.dto.VitalSignDTO;
import com.silvercare.silvercarebackend.mapper.VitalSignMapper;
import com.silvercare.silvercarebackend.service.VitalSignService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/vitals", produces = "application/json")
public class VitalsAliasController {

    private final VitalSignService vitalSignService;

    // GET /api/vitals/{recipientId}?days=7
    @GetMapping("/{recipientId}")
    public List<VitalSignDTO> getByRecipient(
            @PathVariable Long recipientId,
            @RequestParam(defaultValue = "7") int days
    ) {
        return vitalSignService.getByCareRecipient(recipientId, days)
                .stream()
                .map(VitalSignMapper::toDTO)
                .toList();
    }
}

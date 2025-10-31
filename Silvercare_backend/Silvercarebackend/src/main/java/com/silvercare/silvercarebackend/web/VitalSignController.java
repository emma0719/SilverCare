package com.silvercare.silvercarebackend.web;

import com.silvercare.silvercarebackend.domain.CareRecipient;
import com.silvercare.silvercarebackend.domain.User;
import com.silvercare.silvercarebackend.domain.VitalSign;
import com.silvercare.silvercarebackend.dto.VitalSignDTO;
import com.silvercare.silvercarebackend.mapper.VitalSignMapper;
import com.silvercare.silvercarebackend.repository.CareRecipientRepository;
import com.silvercare.silvercarebackend.service.AuditLogService;
import com.silvercare.silvercarebackend.service.UserService;
import com.silvercare.silvercarebackend.service.VitalSignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/vital-signs", produces = "application/json")
@CrossOrigin(
        origins = {"http://localhost:8080", "http://localhost:8082", "http://localhost:8083"},
        allowCredentials = "true",
        allowedHeaders = {"Authorization", "Content-Type", "Accept", "Accept-Language", "Cache-Control", "Pragma"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
public class VitalSignController {

    private final VitalSignService vitalSignService;
    private final UserService userService;
    private final AuditLogService auditLogService;
    private final CareRecipientRepository careRecipientRepository;


    @PreAuthorize("hasAnyRole('ADMIN', 'CAREGIVER', 'FAMILY')")
    @PostMapping
    public ResponseEntity<?> createVitalSign(@RequestBody VitalSignDTO dto, Authentication auth) {
        User user = userService.findByUsername(auth.getName());

        CareRecipient recipient = careRecipientRepository.findById(dto.getCareRecipientId())
                .orElse(null);
        if (recipient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "error", "care_recipient_not_found"));
        }


        if (!canAccessRecipient(user, recipient)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("success", false, "error", "forbidden"));
        }


        VitalSign entity = VitalSignMapper.toEntity(dto);
        VitalSign saved = vitalSignService.save(entity, dto.getCareRecipientId(), user.getId());

        auditLogService.record(user, "CREATE_VITAL_SIGN", "VitalSign", saved.getId());
        log.info("User {} recorded vital sign {} for recipient {}", user.getUsername(), saved.getId(), dto.getCareRecipientId());

        return ResponseEntity.ok(Map.of("success", true, "data", VitalSignMapper.toDTO(saved)));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'CAREGIVER', 'FAMILY')")
    @GetMapping("/{recipientId}")
    public ResponseEntity<?> getVitalSigns(
            @PathVariable Long recipientId,
            @RequestParam(defaultValue = "7") int days,
            Authentication auth
    ) {
        User user = userService.findByUsername(auth.getName());

        CareRecipient recipient = careRecipientRepository.findById(recipientId)
                .orElse(null);
        if (recipient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "error", "care_recipient_not_found"));
        }

        if (!canAccessRecipient(user, recipient)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("success", false, "error", "forbidden"));
        }

        var list = vitalSignService.getByCareRecipient(recipientId, days)
                .stream()
                .map(VitalSignMapper::toDTO)
                .toList();

        auditLogService.record(user, "READ_VITAL_SIGNS", "CareRecipient", recipientId);
        log.info("User {} viewed vital signs for recipient {}", user.getUsername(), recipientId);

        return ResponseEntity.ok(Map.of("success", true, "data", list));
    }


    private boolean canAccessRecipient(User user, CareRecipient recipient) {
        return switch (user.getRole()) {
            case ADMIN -> true;
            case FAMILY -> recipient.getFamily() != null
                    && recipient.getFamily().getId().equals(user.getId());
            case CAREGIVER -> recipient.getUsers() != null
                    && recipient.getUsers().stream().anyMatch(u -> u.getId().equals(user.getId()));
        };
    }
}

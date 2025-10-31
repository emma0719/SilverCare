package com.silvercare.silvercarebackend.web;

import com.silvercare.silvercarebackend.domain.CareRecipient;
import com.silvercare.silvercarebackend.domain.Reminder;
import com.silvercare.silvercarebackend.domain.User;
import com.silvercare.silvercarebackend.dto.ReminderRequest;
import com.silvercare.silvercarebackend.dto.ReminderResponse;
import com.silvercare.silvercarebackend.repository.CareRecipientRepository;
import com.silvercare.silvercarebackend.service.AuditLogService;
import com.silvercare.silvercarebackend.service.ReminderService;
import com.silvercare.silvercarebackend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/reminders", produces = "application/json")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;
    private final CareRecipientRepository careRecipientRepository;
    private final UserService userService;
    private final AuditLogService auditLogService;

    @PreAuthorize("hasAnyRole('ADMIN', 'FAMILY', 'CAREGIVER')")
    @PostMapping
    public ResponseEntity<ReminderResponse> createReminder(@RequestBody ReminderRequest request, Authentication auth) {
        User creator = userService.findByUsername(auth.getName());
        CareRecipient careRecipient = careRecipientRepository.findById(request.getCareRecipientId()).orElseThrow(() -> new IllegalArgumentException("CareRecipient not found"));
        Reminder saved = reminderService.create(request.toEntity(careRecipient), creator);
        auditLogService.record(creator, "CREATE_REMINDER", "Reminder", saved.getId());
        log.info("User {} created reminder {}", creator.getUsername(), saved.getId());
        return ResponseEntity.ok(ReminderResponse.fromEntity(saved));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FAMILY', 'CAREGIVER')")
    @GetMapping("/recipient/{recipientId}")
    public ResponseEntity<List<ReminderResponse>> getByRecipient(@PathVariable Long recipientId) {
        return ResponseEntity.ok(reminderService.getByRecipient(recipientId).stream().map(ReminderResponse::fromEntity).toList());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FAMILY', 'CAREGIVER')")
    @GetMapping("/{id}")
    public ResponseEntity<ReminderResponse> getById(@PathVariable Long id) {
        Reminder reminder = reminderService.getById(id).orElseThrow(() -> new IllegalArgumentException("Reminder not found"));
        return ResponseEntity.ok(ReminderResponse.fromEntity(reminder));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FAMILY', 'CAREGIVER')")
    @PutMapping("/{id}")
    public ResponseEntity<ReminderResponse> updateReminder(@PathVariable Long id, @RequestBody ReminderRequest request, Authentication auth) {
        User user = userService.findByUsername(auth.getName());
        CareRecipient careRecipient = careRecipientRepository.findById(request.getCareRecipientId()).orElseThrow(() -> new IllegalArgumentException("CareRecipient not found"));
        Reminder updated = reminderService.update(id, request.toEntity(careRecipient));
        auditLogService.record(user, "UPDATE_REMINDER", "Reminder", id);
        log.info("User {} updated reminder {}", user.getUsername(), id);
        return ResponseEntity.ok(ReminderResponse.fromEntity(updated));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FAMILY', 'CAREGIVER')")
    @PutMapping("/{id}/days")
    public ResponseEntity<ReminderResponse> updateDays(@PathVariable Long id, @RequestParam Integer daysBits, Authentication auth) {
        User user = userService.findByUsername(auth.getName());
        Reminder updated = reminderService.updateDaysOfWeek(id, daysBits);
        auditLogService.record(user, "UPDATE_DAYS", "Reminder", id);
        log.info("User {} updated reminder days {}", user.getUsername(), id);
        return ResponseEntity.ok(ReminderResponse.fromEntity(updated));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FAMILY', 'CAREGIVER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long id, Authentication auth) {
        User user = userService.findByUsername(auth.getName());
        reminderService.delete(id);
        auditLogService.record(user, "DELETE_REMINDER", "Reminder", id);
        log.info("User {} deleted reminder {}", user.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FAMILY', 'CAREGIVER')")
    @GetMapping
    public ResponseEntity<List<ReminderResponse>> getAllReminders(Authentication auth) {
        User currentUser = userService.findByUsername(auth.getName());
        List<Reminder> reminders;
        switch (currentUser.getRole()) {
            case ADMIN -> reminders = reminderService.getAll();
            case FAMILY -> reminders = reminderService.getByCreatedBy(currentUser);
            case CAREGIVER -> reminders = reminderService.getByCaregiver(currentUser);
            default -> throw new IllegalStateException("Unknown role: " + currentUser.getRole());
        }
        log.info("User {} retrieved {} reminders", currentUser.getUsername(), reminders.size());
        return ResponseEntity.ok(reminders.stream().map(ReminderResponse::fromEntity).toList());
    }
}

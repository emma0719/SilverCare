package com.silvercare.silvercarebackend.web;

import com.silvercare.silvercarebackend.domain.CareRecipient;
import com.silvercare.silvercarebackend.domain.Reminder;
import com.silvercare.silvercarebackend.dto.ReminderRequest;
import com.silvercare.silvercarebackend.dto.ReminderResponse;
import com.silvercare.silvercarebackend.repository.CareRecipientRepository;
import com.silvercare.silvercarebackend.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;
    private final CareRecipientRepository careRecipientRepository;

    // Create
    @PostMapping
    public ResponseEntity<ReminderResponse> createReminder(@RequestBody ReminderRequest request) {
        CareRecipient careRecipient = careRecipientRepository.findById(request.getCareRecipientId())
                .orElseThrow(() -> new IllegalArgumentException("CareRecipient not found"));

        Reminder saved = reminderService.create(request.toEntity(careRecipient));
        return ResponseEntity.ok(ReminderResponse.fromEntity(saved));
    }

    // Read by recipient
    @GetMapping("/recipient/{recipientId}")
    public ResponseEntity<List<ReminderResponse>> getByRecipient(@PathVariable Long recipientId) {
        return ResponseEntity.ok(
                reminderService.getByRecipient(recipientId)
                        .stream()
                        .map(ReminderResponse::fromEntity)
                        .toList()
        );
    }

    // Read one by id
    @GetMapping("/{id}")
    public ResponseEntity<ReminderResponse> getById(@PathVariable Long id) {
        Reminder reminder = reminderService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reminder not found"));
        return ResponseEntity.ok(ReminderResponse.fromEntity(reminder));
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<ReminderResponse> updateReminder(
            @PathVariable Long id,
            @RequestBody ReminderRequest request
    ) {
        CareRecipient careRecipient = careRecipientRepository.findById(request.getCareRecipientId())
                .orElseThrow(() -> new IllegalArgumentException("CareRecipient not found"));

        Reminder updated = reminderService.update(id, request.toEntity(careRecipient));
        return ResponseEntity.ok(ReminderResponse.fromEntity(updated));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long id) {
        reminderService.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}

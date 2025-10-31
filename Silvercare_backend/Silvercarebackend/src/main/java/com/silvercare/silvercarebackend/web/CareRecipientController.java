package com.silvercare.silvercarebackend.web;

import com.silvercare.silvercarebackend.domain.CareRecipient;
import com.silvercare.silvercarebackend.domain.User;
import com.silvercare.silvercarebackend.repository.CareRecipientRepository;
import com.silvercare.silvercarebackend.repository.UserRepository;
import com.silvercare.silvercarebackend.service.CareRecipientService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/care-recipients", produces = "application/json")
public class CareRecipientController {

    private final CareRecipientRepository careRecipientRepository;
    private final CareRecipientService careRecipientService;
    private final UserRepository userRepository;

    // ---------------------------------------------------------
    // GET ALL
    // ---------------------------------------------------------
    @GetMapping
    public ResponseEntity<?> getAllRecipients(Authentication auth) {
        User user = getCurrentUser(auth);

        List<CareRecipient> list = switch (user.getRole()) {
            case ADMIN -> careRecipientRepository.findAll();
            case FAMILY -> careRecipientRepository.findByFamilyId(user.getId());
            case CAREGIVER -> careRecipientRepository.findByUsersContaining(user);
        };

        return ResponseEntity.ok(Map.of("success", true, "data", list));
    }

    // ---------------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipient(@PathVariable Long id, Authentication auth) {
        User user = getCurrentUser(auth);

        return careRecipientRepository.findById(id)
                .map(recipient -> canAccessRecipient(user, recipient)
                        ? ResponseEntity.ok(Map.of("success", true, "data", recipient))
                        : ResponseEntity.status(403).body(Map.of("success", false, "error", "forbidden")))
                .orElse(ResponseEntity.status(404).body(Map.of("success", false, "error", "not_found")));
    }

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------
    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> createRecipient(@RequestBody CareRecipient body, Authentication auth) {
        User user = getCurrentUser(auth);

        if (!(user.getRole() == User.Role.ADMIN || user.getRole() == User.Role.FAMILY)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "error", "forbidden"));
        }

        // ✅ 绑定归属用户（FAMILY）
        if (user.getRole() == User.Role.FAMILY) {
            body.setFamily(user);
        }

        body.setCreatedBy(user);

        CareRecipient saved = careRecipientRepository.save(body);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "data", saved));
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> updateRecipient(
            @PathVariable Long id,
            @RequestBody CareRecipient body,
            Authentication auth
    ) {
        User user = getCurrentUser(auth);
        var existing = careRecipientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CareRecipient not found"));

        if (!canAccessRecipient(user, existing)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "error", "forbidden"));
        }

        CareRecipient updated = careRecipientService.updateBasic(id, body);
        return ResponseEntity.ok(Map.of("success", true, "data", updated));
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipient(
            @PathVariable Long id,
            @RequestParam(name = "cascade", defaultValue = "false") boolean cascade,
            Authentication auth
    ) {
        User user = getCurrentUser(auth);
        var recipientOpt = careRecipientRepository.findById(id);

        if (recipientOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("success", false, "error", "not_found"));
        }

        var recipient = recipientOpt.get();

        // ✅ FAMILY 也可以删除属于自己的受照顾者
        boolean canDelete = switch (user.getRole()) {
            case ADMIN -> true;
            case FAMILY -> recipient.getFamily() != null && recipient.getFamily().getId().equals(user.getId());
            case CAREGIVER -> false;
        };

        if (!canDelete) {
            return ResponseEntity.status(403).body(Map.of("success", false, "error", "forbidden"));
        }

        try {
            if (cascade) {
                careRecipientService.deleteCascade(id);
            } else {
                careRecipientService.deleteOnlyRecipient(id);
            }
            return ResponseEntity.ok(Map.of("success", true, "message", "deleted"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of(
                            "success", false,
                            "error", "Cannot delete: this recipient has dependent vital records.",
                            "hint", "Retry with ?cascade=true to remove dependent vital_signs automatically."
                    )
            );
        }
    }

    // ---------------------------------------------------------
    // PRIVATE HELPERS
    // ---------------------------------------------------------
    private boolean canAccessRecipient(User user, CareRecipient recipient) {
        return switch (user.getRole()) {
            case ADMIN -> true;
            case FAMILY -> (recipient.getFamily() != null && recipient.getFamily().getId().equals(user.getId()))
                    || (recipient.getCreatedBy() != null && recipient.getCreatedBy().getId().equals(user.getId()));
            case CAREGIVER -> recipient.getUsers() != null
                    && recipient.getUsers().stream().anyMatch(u -> u.getId().equals(user.getId()));
        };
    }

    private User getCurrentUser(Authentication auth) {
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}

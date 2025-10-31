package com.silvercare.silvercarebackend.service;

import com.silvercare.silvercarebackend.domain.CareRecipient;
import com.silvercare.silvercarebackend.domain.User;
import com.silvercare.silvercarebackend.domain.VitalSign;
import com.silvercare.silvercarebackend.repository.CareRecipientRepository;
import com.silvercare.silvercarebackend.repository.UserRepository;
import com.silvercare.silvercarebackend.repository.VitalSignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VitalSignService {

    private final VitalSignRepository vitalSignRepository;
    private final CareRecipientRepository careRecipientRepository;
    private final UserRepository userRepository;

    @Transactional
    public VitalSign save(VitalSign vitalSign, Long recipientId, Long userId) {
        CareRecipient recipient = careRecipientRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("CareRecipient not found: " + recipientId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // ✅ 权限控制（仅允许 Admin / Family 自己 / Caregiver 自己）
        if (!canAccessRecipient(user, recipient)) {
            throw new SecurityException("Forbidden: user not allowed to record vitals for this recipient");
        }

        vitalSign.setCareRecipient(recipient);
        vitalSign.setRecordedBy(user);
        return vitalSignRepository.save(vitalSign);
    }


    @Transactional(readOnly = true)
    public List<VitalSign> getByCareRecipient(Long recipientId, int days) {
        int safeDays = days > 0 ? days : 7;
        OffsetDateTime fromTime = OffsetDateTime.now().minusDays(safeDays);
        return vitalSignRepository.findByCareRecipient_IdAndRecordedAtAfterOrderByRecordedAtAsc(recipientId, fromTime);
    }

    @Transactional(readOnly = true)
    public List<VitalSign> getLatest(Long recipientId) {
        return vitalSignRepository.findByCareRecipient_IdOrderByRecordedAtDesc(recipientId);
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

package com.silvercare.silvercarebackend.service;

import com.silvercare.silvercarebackend.domain.CareRecipient;
import com.silvercare.silvercarebackend.domain.User;
import com.silvercare.silvercarebackend.domain.VitalSign;
import com.silvercare.silvercarebackend.repository.CareRecipientRepository;
import com.silvercare.silvercarebackend.repository.UserRepository;
import com.silvercare.silvercarebackend.repository.VitalSignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VitalSignService {

    private final VitalSignRepository vitalSignRepository;
    private final CareRecipientRepository careRecipientRepository;
    private final UserRepository userRepository;

    public VitalSign save(VitalSign vitalSign, Long recipientId, Long userId) {
        CareRecipient recipient = careRecipientRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("CareRecipient not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        vitalSign.setCareRecipient(recipient);
        vitalSign.setRecordedBy(user);
        return vitalSignRepository.save(vitalSign);
    }

    // 查询某个受照顾者的体征，默认最近 N 天
    public List<VitalSign> getByCareRecipient(Long recipientId, int days) {
        OffsetDateTime fromTime = OffsetDateTime.now().minusDays(days);
        return vitalSignRepository.findByCareRecipient_IdAndRecordedAtAfterOrderByRecordedAtAsc(recipientId, fromTime);
    }
}

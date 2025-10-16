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

        vitalSign.setCareRecipient(recipient);
        vitalSign.setRecordedBy(user);
        return vitalSignRepository.save(vitalSign);
    }

    // 查询某个受照顾者在最近 N 天的体征（N<=0 时默认 7 天）
    @Transactional(readOnly = true)
    public List<VitalSign> getByCareRecipient(Long recipientId, int days) {
        int safeDays = days > 0 ? days : 7;
        OffsetDateTime fromTime = OffsetDateTime.now().minusDays(safeDays);
        return vitalSignRepository.findByCareRecipient_IdAndRecordedAtAfterOrderByRecordedAtAsc(recipientId, fromTime);
    }

    // 可选：获取该受照顾者最新记录（降序）
    @Transactional(readOnly = true)
    public List<VitalSign> getLatest(Long recipientId) {
        return vitalSignRepository.findByCareRecipient_IdOrderByRecordedAtDesc(recipientId);
    }
}

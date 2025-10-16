package com.silvercare.silvercarebackend.service;

import com.silvercare.silvercarebackend.domain.CareRecipient;
import com.silvercare.silvercarebackend.repository.CareRecipientRepository;
import com.silvercare.silvercarebackend.repository.VitalSignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CareRecipientService {

    private final CareRecipientRepository careRecipientRepository;
    private final VitalSignRepository vitalSignRepository;

    /**
     * 强制级联删除：先清 vital_signs，再删 care_recipient。
     * 全流程放在同一事务里，确保要么都成功，要么都回滚。
     */
    @Transactional
    public void deleteCascade(Long recipientId) {
        // 若不存在会在 deleteById 时抛异常，这里允许“先删子表再删父表”，即使子表不存在也无妨
        vitalSignRepository.deleteByCareRecipientId(recipientId);
        careRecipientRepository.deleteById(recipientId);
    }

    /**
     * 安全删除（不清理 vital_signs）。如果有外键引用，将由控制器捕获并返回 409。
     */
    public void deleteOnlyRecipient(Long recipientId) {
        try {
            careRecipientRepository.deleteById(recipientId);
        } catch (EmptyResultDataAccessException ignore) {
            // 删除一个不存在的 ID：保持语义幂等，交给控制器决定返回码（通常 204/404 均可）
        }
    }

    /**
     * 简单更新示例（如果你需要的话，可在 Controller 调用）
     */
    @Transactional
    public CareRecipient updateBasic(Long id, CareRecipient body) {
        CareRecipient existing = careRecipientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CareRecipient not found: " + id));
        existing.setFullName(body.getFullName());
        existing.setPhoneNumber(body.getPhoneNumber());
        existing.setAddress(body.getAddress());
        existing.setAge(body.getAge());
        existing.setActive(body.getActive());
        return careRecipientRepository.save(existing);
    }
}

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

    @Transactional
    public void deleteCascade(Long recipientId) {
        vitalSignRepository.deleteByCareRecipientId(recipientId);
        careRecipientRepository.deleteById(recipientId);
    }

    public void deleteOnlyRecipient(Long recipientId) {
        try {
            careRecipientRepository.deleteById(recipientId);
        } catch (EmptyResultDataAccessException ignore) {}
    }

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

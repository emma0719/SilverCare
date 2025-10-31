package com.silvercare.silvercarebackend.repository;

import com.silvercare.silvercarebackend.domain.VitalSign;
import com.silvercare.silvercarebackend.domain.CareRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface VitalSignRepository extends JpaRepository<VitalSign, Long> {
    List<VitalSign> findByCareRecipient(CareRecipient careRecipient);
    List<VitalSign> findByCareRecipient_IdOrderByRecordedAtDesc(Long careRecipientId);
    List<VitalSign> findByCareRecipient_IdAndRecordedAtAfterOrderByRecordedAtAsc(Long careRecipientId, java.time.OffsetDateTime fromTime);

    @Transactional
    void deleteByCareRecipientId(Long careRecipientId);
}

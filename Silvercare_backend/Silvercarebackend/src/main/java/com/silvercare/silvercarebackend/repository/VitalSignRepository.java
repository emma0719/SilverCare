package com.silvercare.silvercarebackend.repository;

import com.silvercare.silvercarebackend.domain.VitalSign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface VitalSignRepository extends JpaRepository<VitalSign, Long> {

    // 原有方法
    List<VitalSign> findByCareRecipientIdOrderByRecordedAtDesc(Long careRecipientId);


    List<VitalSign> findByCareRecipient_IdAndRecordedAtAfterOrderByRecordedAtAsc(
            Long careRecipientId,
            OffsetDateTime fromTime
    );
}

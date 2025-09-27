package com.silvercare.silvercarebackend.repository;

import com.silvercare.silvercarebackend.domain.VitalSign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VitalSignRepository extends JpaRepository<VitalSign, Long> {
    List<VitalSign> findByCareRecipientIdOrderByRecordedAtDesc(Long careRecipientId);
}

package com.silvercare.silvercarebackend.repository;

import com.silvercare.silvercarebackend.domain.VitalSign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface VitalSignRepository extends JpaRepository<VitalSign, Long> {

    // 最近记录（按 recordedAt 降序）
    List<VitalSign> findByCareRecipient_IdOrderByRecordedAtDesc(Long careRecipientId);

    // 指定时间之后的记录（按 recordedAt 升序，便于前端画时序图）
    List<VitalSign> findByCareRecipient_IdAndRecordedAtAfterOrderByRecordedAtAsc(
            Long careRecipientId,
            OffsetDateTime fromTime
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("DELETE FROM VitalSign v WHERE v.careRecipient.id = :recipientId")
    void deleteByCareRecipientId(@Param("recipientId") Long recipientId);
}

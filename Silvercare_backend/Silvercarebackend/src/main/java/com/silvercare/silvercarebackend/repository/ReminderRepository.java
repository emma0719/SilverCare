package com.silvercare.silvercarebackend.repository;

import com.silvercare.silvercarebackend.domain.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByCareRecipientIdAndActive(Long careRecipientId, Boolean active);
}


package com.silvercare.silvercarebackend.repository;

import com.silvercare.silvercarebackend.domain.Reminder;
import com.silvercare.silvercarebackend.domain.User;
import com.silvercare.silvercarebackend.domain.CareRecipient;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByCareRecipientIdAndActive(Long careRecipientId, Boolean active);
    @EntityGraph(attributePaths = "careRecipient")
    @Query("select r from Reminder r")
    List<Reminder> findAllWithCareRecipient();
    List<Reminder> findByActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate start, LocalDate end);
    List<Reminder> findByCreatedBy(User user);
    List<Reminder> findByCareRecipient(CareRecipient careRecipient);
}

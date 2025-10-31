package com.silvercare.silvercarebackend.service;

import com.silvercare.silvercarebackend.domain.Reminder;
import com.silvercare.silvercarebackend.domain.User;
import java.util.List;
import java.util.Optional;

public interface ReminderService {
    Reminder create(Reminder reminder);
    List<Reminder> getByRecipient(Long recipientId);
    void delete(Long id);
    Optional<Reminder> getById(Long id);
    Reminder update(Long id, Reminder reminder);
    List<Reminder> getAll();
    Reminder updateDaysOfWeek(Long id, Integer daysBits);
    Reminder create(Reminder reminder, User createdBy);
    List<Reminder> getByCreatedBy(User user);
    List<Reminder> getByCaregiver(User caregiver);
}

package com.silvercare.silvercarebackend.service;

import com.silvercare.silvercarebackend.domain.Reminder;
import java.util.List;
import java.util.Optional;

public interface ReminderService {
    Reminder create(Reminder reminder);
    List<Reminder> getByRecipient(Long recipientId);
    void delete(Long id);
    Optional<Reminder> getById(Long id);
    Reminder update(Long id, Reminder reminder);
}

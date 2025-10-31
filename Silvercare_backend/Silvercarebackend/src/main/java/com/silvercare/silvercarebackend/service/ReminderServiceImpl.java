package com.silvercare.silvercarebackend.service;

import com.silvercare.silvercarebackend.domain.CareRecipient;
import com.silvercare.silvercarebackend.domain.Reminder;
import com.silvercare.silvercarebackend.domain.User;
import com.silvercare.silvercarebackend.repository.CareRecipientRepository;
import com.silvercare.silvercarebackend.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;
    private final CareRecipientRepository careRecipientRepository;

    @Override
    public Reminder create(Reminder reminder) {
        return reminderRepository.save(reminder);
    }

    @Override
    public Reminder create(Reminder reminder, User createdBy) {
        reminder.setCreatedBy(createdBy);
        return reminderRepository.save(reminder);
    }

    @Override
    public List<Reminder> getByRecipient(Long recipientId) {
        return reminderRepository.findByCareRecipientIdAndActive(recipientId, true);
    }

    @Override
    public void delete(Long id) {
        reminderRepository.deleteById(id);
    }

    @Override
    public Optional<Reminder> getById(Long id) {
        return reminderRepository.findById(id);
    }

    @Override
    public Reminder update(Long id, Reminder reminder) {
        Reminder existing = reminderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Reminder not found"));
        existing.setMedTitle(reminder.getMedTitle());
        existing.setDosageText(reminder.getDosageText());
        existing.setRepeatType(reminder.getRepeatType());
        existing.setDaysOfWeekBits(reminder.getDaysOfWeekBits());
        existing.setTimePoints(reminder.getTimePoints());
        existing.setStartDate(reminder.getStartDate());
        existing.setEndDate(reminder.getEndDate());
        existing.setActive(reminder.getActive());
        existing.setCareRecipient(reminder.getCareRecipient());
        return reminderRepository.save(existing);
    }

    @Override
    public List<Reminder> getAll() {
        return reminderRepository.findAllWithCareRecipient();
    }

    @Override
    public Reminder updateDaysOfWeek(Long id, Integer daysBits) {
        Reminder existing = reminderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Reminder not found"));
        existing.setDaysOfWeekBits(daysBits);
        return reminderRepository.save(existing);
    }

    @Override
    public List<Reminder> getByCreatedBy(User user) {
        return reminderRepository.findByCreatedBy(user);
    }

    @Override
    public List<Reminder> getByCaregiver(User caregiver) {
        List<CareRecipient> recipients = careRecipientRepository.findByUsersContaining(caregiver);
        return recipients.stream().flatMap(recipient -> reminderRepository.findByCareRecipient(recipient).stream()).toList();
    }
}

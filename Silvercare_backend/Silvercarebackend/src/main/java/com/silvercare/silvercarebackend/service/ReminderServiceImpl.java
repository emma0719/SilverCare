package com.silvercare.silvercarebackend.service;

import com.silvercare.silvercarebackend.domain.Reminder;
import com.silvercare.silvercarebackend.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;

    @Override
    public Reminder create(Reminder reminder) {
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
        Reminder existing = reminderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reminder not found"));

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
    public Reminder updateDaysOfWeek(Long id, Integer daysBits) {
        return null;
    }
}

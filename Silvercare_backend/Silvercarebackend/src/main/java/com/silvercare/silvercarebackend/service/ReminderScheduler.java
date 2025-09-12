package com.silvercare.silvercarebackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.silvercare.silvercarebackend.config.ReminderProperties;
import com.silvercare.silvercarebackend.domain.Reminder;
import com.silvercare.silvercarebackend.repository.ReminderRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final ReminderRepository reminderRepository;
    private final ReminderProperties reminderProperties;
    private final SmsService smsService;
    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void runAtStartup() {
        log.info("🚀 Running reminder check at startup...");
        checkReminders();
    }

    @Scheduled(cron = "0 * * * * *") // 每分钟跑一次
    public void checkReminders() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        int tolerance = reminderProperties.getToleranceMinutes();

        List<Reminder> reminders = reminderRepository.findAllWithCareRecipient();

        reminders.stream()
                .filter(r -> Boolean.TRUE.equals(r.getActive()))
                .filter(r -> !today.isBefore(r.getStartDate()) &&
                        (r.getEndDate() == null || !today.isAfter(r.getEndDate())))
                .forEach(r -> {
                    try {
                        List<String> times = mapper.readValue(r.getTimePoints(),
                                new TypeReference<>() {});
                        times.stream()
                                .map(LocalTime::parse)
                                .filter(time -> Math.abs(Duration.between(time, now).toMinutes()) <= tolerance)
                                .forEach(time -> {

                                    log.info("💊 It’s time for {} — {}. Please take your dose now.",
                                            r.getMedTitle(),
                                            r.getDosageText());

                                   // add sms
                                    String msg = "💊 Reminder: " + r.getMedTitle() + " — " + r.getDosageText()
                                            + " (Time: " + time + ")";
                                    smsService.sendSms(r.getCareRecipient().getPhoneNumber(), msg);
                                });
                    } catch (Exception e) {
                        log.error("❌ Failed to parse timePoints: {}", r.getTimePoints(), e);
                    }
                });
    }
}
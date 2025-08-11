package com.silvercare.silvercarebackend;

import com.silvercare.silvercarebackend.domain.CareRecipient;
import com.silvercare.silvercarebackend.repository.CareRecipientRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//@Component
public class DataInitRunner implements CommandLineRunner {

    private final CareRecipientRepository repository;

    public DataInitRunner(CareRecipientRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        CareRecipient cr = CareRecipient.builder()
                .name("张三")
                .gender("Male")
                .roomNo("101")
                .allergies("花粉")
                .conditions("高血压")
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(cr);

        System.out.println("CareRecipient 已保存，总数：" + repository.count());
    }
}

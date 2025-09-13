package com.silvercare.silvercarebackend.web;

import com.silvercare.silvercarebackend.domain.CareRecipient;
import com.silvercare.silvercarebackend.repository.CareRecipientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/care-recipients", produces = "application/json")
public class CareRecipientController {

    private final CareRecipientRepository careRecipientRepository;

    // 获取所有 care recipients
    @GetMapping
    public List<CareRecipient> getAllRecipients() {
        return careRecipientRepository.findAll();
    }

    // 根据 ID 获取一个
    @GetMapping("/{id}")
    public CareRecipient getRecipient(@PathVariable Long id) {
        return careRecipientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CareRecipient not found"));
    }

    // 添加一个
    @PostMapping
    public CareRecipient createRecipient(@RequestBody CareRecipient careRecipient) {
        return careRecipientRepository.save(careRecipient);
    }
}

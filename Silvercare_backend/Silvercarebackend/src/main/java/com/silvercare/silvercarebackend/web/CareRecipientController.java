package com.silvercare.silvercarebackend.web;

import com.silvercare.silvercarebackend.domain.CareRecipient;
import com.silvercare.silvercarebackend.repository.CareRecipientRepository;
import com.silvercare.silvercarebackend.service.CareRecipientService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/care-recipients", produces = "application/json")
public class CareRecipientController {

    private final CareRecipientRepository careRecipientRepository;
    private final CareRecipientService careRecipientService;

    // 获取所有
    @GetMapping
    public List<CareRecipient> getAllRecipients() {
        return careRecipientRepository.findAll();
    }

    // 根据 ID 获取
    @GetMapping("/{id}")
    public CareRecipient getRecipient(@PathVariable Long id) {
        return careRecipientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CareRecipient not found: " + id));
    }

    // 新增
    @PostMapping(consumes = "application/json")
    public ResponseEntity<CareRecipient> createRecipient(@RequestBody CareRecipient body) {
        CareRecipient saved = careRecipientRepository.save(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 更新（示例：基础字段全量覆盖）
    @PutMapping(value = "/{id}", consumes = "application/json")
    public CareRecipient updateRecipient(@PathVariable Long id, @RequestBody CareRecipient body) {
        return careRecipientService.updateBasic(id, body);
    }

    /**
     * 删除：
     * - 普通删除：DELETE /api/care-recipients/{id}
     *   - 若存在外键引用（有 vital_signs），返回 409，并提示可用 ?cascade=true
     * - 强制删除：DELETE /api/care-recipients/{id}?cascade=true
     *   - 事务内先删子表再删父表
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipient(
            @PathVariable Long id,
            @RequestParam(name = "cascade", defaultValue = "false") boolean cascade
    ) {
        if (cascade) {
            careRecipientService.deleteCascade(id);
            return ResponseEntity.noContent().build();
        }

        try {
            careRecipientService.deleteOnlyRecipient(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            // 外键约束拦截：返回 409 + 明确提示
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of(
                            "error", "Cannot delete: this recipient has vital records.",
                            "hint", "Retry with ?cascade=true to remove dependent vital_signs automatically."
                    )
            );
        }
    }
}

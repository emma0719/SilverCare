package com.silvercare.silvercarebackend.web;

import com.silvercare.silvercarebackend.domain.CareRecipient;
import com.silvercare.silvercarebackend.dto.CareRecipientCreateReq;
import com.silvercare.silvercarebackend.dto.CareRecipientDto;
import com.silvercare.silvercarebackend.repository.CareRecipientRepository;
import com.silvercare.silvercarebackend.web.error.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/care-recipients")
public class CareRecipientController {

    private final CareRecipientRepository repo;

    public CareRecipientController(CareRecipientRepository repo) {
        this.repo = repo;
    }

    // 查询全部
    @GetMapping
    public List<CareRecipientDto> list() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    // 按 id 查询
    @GetMapping("{id}")
    public CareRecipientDto get(@PathVariable Long id) {
        CareRecipient cr = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("CareRecipient not found"));
        return toDto(cr);
    }

    // 新增
    @PostMapping
    public CareRecipientDto create(@Valid @RequestBody CareRecipientCreateReq req) {
        CareRecipient e = new CareRecipient();
        e.setName(req.name());
        e.setGender(req.gender());
        e.setDob(req.dob());
        e.setRoomNo(req.roomNo());
        e.setAllergies(req.allergies());
        e.setConditions(req.conditions());
        e.setCreatedAt(LocalDateTime.now());

        e = repo.save(e);
        return toDto(e);
    }

    // 映射到 DTO
    private CareRecipientDto toDto(CareRecipient e) {
        return new CareRecipientDto(
                e.getId(),
                e.getName(),
                e.getGender(),
                e.getDob(),
                e.getRoomNo(),
                e.getAllergies(),
                e.getConditions(),
                e.getCreatedAt()
        );
    }
}

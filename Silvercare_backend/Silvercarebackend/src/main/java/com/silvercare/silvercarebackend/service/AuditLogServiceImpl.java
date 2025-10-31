package com.silvercare.silvercarebackend.service;

import com.silvercare.silvercarebackend.domain.AuditLog;
import com.silvercare.silvercarebackend.domain.User;
import com.silvercare.silvercarebackend.repository.AuditLogRepository;
import com.silvercare.silvercarebackend.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public AuditLog record(User user, String action, String entityType, Long entityId) {
        AuditLog log = AuditLog.builder()
                .user(user)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .build();
        return auditLogRepository.save(log);
    }

    @Override
    public List<AuditLog> getAll() {
        return auditLogRepository.findAll();
    }
}

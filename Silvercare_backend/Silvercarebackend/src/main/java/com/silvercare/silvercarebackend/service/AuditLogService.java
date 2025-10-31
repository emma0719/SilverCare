package com.silvercare.silvercarebackend.service;

import com.silvercare.silvercarebackend.domain.AuditLog;
import com.silvercare.silvercarebackend.domain.User;
import java.util.List;

public interface AuditLogService {
    AuditLog record(User user, String action, String entityType, Long entityId);
    List<AuditLog> getAll();
}

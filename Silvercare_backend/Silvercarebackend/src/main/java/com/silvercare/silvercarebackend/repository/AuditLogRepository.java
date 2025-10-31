package com.silvercare.silvercarebackend.repository;

import com.silvercare.silvercarebackend.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}

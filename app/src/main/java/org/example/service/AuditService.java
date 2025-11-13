package org.example.service;

import java.time.LocalDateTime;
import org.example.model.AuditAction;
import org.example.model.AuditLog;
import org.example.repository.AuditRepository;

public class AuditService {
  private final AuditRepository auditRepository;

  public AuditService(AuditRepository auditRepository) {
    this.auditRepository = auditRepository;
  }

  public void logAction(String username, AuditAction action, String details) {
    if (username == null || action == null) {
      return;
    }
    AuditLog auditLog = new AuditLog(LocalDateTime.now(), username, action, details);
    auditRepository.save(auditLog);
  }
}

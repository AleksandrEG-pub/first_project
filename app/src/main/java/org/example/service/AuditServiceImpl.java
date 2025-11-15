package org.example.service;

import java.time.LocalDateTime;
import org.example.model.AuditAction;
import org.example.model.AuditLog;
import org.example.repository.AuditRepository;

public class AuditServiceImpl implements AuditService {
  private final AuditRepository auditRepository;

  public AuditServiceImpl(AuditRepository auditRepository) {
    this.auditRepository = auditRepository;
  }

  @Override
  public void logAction(String username, AuditAction action, String details) {
    if (username == null || action == null) {
      return;
    }
    AuditLog auditLog = new AuditLog();
    auditLog.setTimestamp(LocalDateTime.now());
    auditLog.setUsername(username);
    auditLog.setAction(action);
    auditLog.setDetails(details);
    auditRepository.save(auditLog);
  }
}

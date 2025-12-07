package org.example_audit.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example_audit.model.AuditAction;
import org.example_audit.model.AuditLog;
import org.example_audit.repository.AuditRepository;
import org.example_audit.service.AuditService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {
  private final AuditRepository auditRepository;

  @Override
  public void logAction(String username, AuditAction action, String details, String resource) {
    AuditLog auditLog = new AuditLog();
    auditLog.setTimestamp(LocalDateTime.now());
    auditLog.setUsername(username);
    auditLog.setAction(action);
    auditLog.setDetails(details);
    auditLog.setResource(resource);
    auditRepository.save(auditLog);
  }

  @Override
  public List<AuditLog> findAll() {
    return auditRepository.findAll();
  }

  @Override
  public List<AuditLog> findByUsername(String username) {
    return auditRepository.findByUsername(username);
  }
}

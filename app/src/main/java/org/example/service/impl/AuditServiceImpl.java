package org.example.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import org.example.model.AuditAction;
import org.example.model.AuditLog;
import org.example.model.User;
import org.example.repository.AuditRepository;
import org.example.service.AuditService;
import org.springframework.stereotype.Component;

@Component
public class AuditServiceImpl implements AuditService {
  private final AuditRepository auditRepository;

  public AuditServiceImpl(AuditRepository auditRepository) {
    this.auditRepository = auditRepository;
    AuditEvents.addListener(
        auditEvent -> logAction(UserContext::getValidatedCurrentUser, auditEvent.getAuditAction(), auditEvent.getDetails()));
  }

  @Override
  public void logAction(Supplier<User> userSupplier, AuditAction action, String details) {
    User user = userSupplier.get();
    if (user == null || action == null || user.getUsername() == null) {
      return;
    }
    AuditLog auditLog = new AuditLog();
    auditLog.setTimestamp(LocalDateTime.now());
    auditLog.setUsername(user.getUsername());
    auditLog.setAction(action);
    auditLog.setDetails(details);
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

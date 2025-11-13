package org.example.repository.impl.in_memory;

import java.util.ArrayList;
import java.util.List;
import org.example.model.AuditLog;
import org.example.repository.AuditRepository;

public class InMemoryAuditRepository implements AuditRepository {
  private final List<AuditLog> auditLogs;

  public InMemoryAuditRepository() {
    this.auditLogs = new ArrayList<>();
  }

  @Override
  public AuditLog save(AuditLog auditLog) {
    if (auditLog == null) {
      throw new IllegalArgumentException("AuditLog cannot be null");
    }
    auditLogs.add(auditLog);
    return auditLog;
  }

  @Override
  public List<AuditLog> findByUsername(String username) {
    if (username == null) {
      return new ArrayList<>();
    }
    return auditLogs.stream()
        .filter(log -> username.equals(log.getUsername()))
        .toList();
  }

  @Override
  public List<AuditLog> findAll() {
    return new ArrayList<>(auditLogs);
  }
}

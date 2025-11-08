package org.example.repository;

import org.example.model.AuditLog;

import java.util.List;

public interface AuditRepository {
    AuditLog save(AuditLog auditLog);
    List<AuditLog> findByUsername(String username);
    List<AuditLog> findAll();
}


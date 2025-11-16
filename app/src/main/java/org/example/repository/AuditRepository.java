package org.example.repository;

import java.util.List;
import org.example.model.AuditLog;

/**
 * Repository abstraction for recording audit events and actions.
 *
 * This repository is append-oriented: implementations persist audit records that describe
 * significant application events (user actions, data changes, system events).
 */
public interface AuditRepository {
  AuditLog save(AuditLog auditLog);

  List<AuditLog> findByUsername(String username);

  List<AuditLog> findAll();
}

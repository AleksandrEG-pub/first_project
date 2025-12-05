package org.example_audit.service;

import java.util.List;
import org.example_audit.model.AuditAction;
import org.example_audit.model.AuditLog;

/** Service for auditing user activities. */
public interface AuditService {

  /** Stores an audit log of a user action. */
  void logAction(String username, AuditAction action, String details, String resource);

  /** Returns all audit logs. */
  List<AuditLog> findAll();

  /** Finds audit logs by username. */
  List<AuditLog> findByUsername(String username);
}

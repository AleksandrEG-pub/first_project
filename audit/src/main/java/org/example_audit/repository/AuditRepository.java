package org.example_audit.repository;

import java.util.List;
import org.example_audit.model.AuditLog;

/**
 * Repository abstraction for recording audit events and actions.
 * This repository is append-oriented: implementations persist audit records that describe
 * significant application events (user actions, data changes, system events).
 */
public interface AuditRepository {

  /**
   * Saves an audit log record.
   *
   * @param auditLog the audit record to save
   * @return the saved audit record
   */
  AuditLog save(AuditLog auditLog);

  /**
   * Finds audit logs by username.
   *
   * @param username the username to search for
   * @return list of audit logs for the user
   */
  List<AuditLog> findByUsername(String username);

  /**
   * Gets all audit logs.
   *
   * @return all audit records
   */
  List<AuditLog> findAll();
}

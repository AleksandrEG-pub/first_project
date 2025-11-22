package org.example.service;

import java.util.List;
import org.example.model.AuditAction;
import org.example.model.AuditLog;

/** Actions related to auditing user activities. */
public interface AuditService {

  /**
   * Stores an audit log of a user action.
   *
   * @param username who performed the action
   * @param action the action performed
   * @param details arbitrary information about the action
   */
  void logAction(String username, AuditAction action, String details);

  List<AuditLog> findAll();

  List<AuditLog> findByUsername(String username);
}

package org.example.service;

import java.util.List;
import java.util.function.Supplier;

import org.example.model.AuditAction;
import org.example.model.AuditLog;
import org.example.model.User;

/** Actions related to auditing user activities. */
public interface AuditService {

  /**
   * Stores an audit log of a user action.
   *
   * @param userSupplier who performed the action
   * @param action the action performed
   * @param details arbitrary information about the action
   */
  void logAction(Supplier<User> userSupplier, AuditAction action, String details);

  List<AuditLog> findAll();

  List<AuditLog> findByUsername(String username);
}

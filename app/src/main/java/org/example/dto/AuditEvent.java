package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.model.AuditAction;

@AllArgsConstructor
@Getter
public class AuditEvent {
  /** Perform action */
  private AuditAction auditAction;

  /**
   * Additional information about action, for example for search, can be provided used 'id', or
   * 'name'
   */
  private String details;
}

package org.example_audit.model;

import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class AuditLog {
  private Long id;
  private LocalDateTime timestamp;
  private String username;
  private AuditAction action;
  private String resource;
  private String details;

  public static AuditLog fromAuditLog(AuditLog other) {
    AuditLog auditLog = new AuditLog();
    auditLog.setId(other.getId());
    auditLog.setTimestamp(other.getTimestamp());
    auditLog.setUsername(other.getUsername());
    auditLog.setAction(other.getAction());
    auditLog.setDetails(other.getDetails());
    auditLog.setResource(other.getResource());
    return auditLog;
  }

  @Override
  public String toString() {
    return String.format(
        "AuditLog{timestamp=%s,username='%s',action=%s,details='%s'}",
        timestamp, username, action, details);
  }
}

package org.example_audit.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
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
    return auditLog;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AuditLog auditLog = (AuditLog) o;
    return Objects.equals(timestamp, auditLog.timestamp)
        && Objects.equals(username, auditLog.username)
        && action == auditLog.action
        && Objects.equals(details, auditLog.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, username, action, details);
  }

  @Override
  public String toString() {
    return String.format(
        "AuditLog{timestamp=%s,username='%s',action=%s,details='%s'}",
        timestamp, username, action, details);
  }
}

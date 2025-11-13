package org.example.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class AuditLog {
  private LocalDateTime timestamp;
  private String username;
  private AuditAction action;
  private String details;

  public AuditLog() {}

  public AuditLog(LocalDateTime timestamp, String username, AuditAction action, String details) {
    this.timestamp = timestamp;
    this.username = username;
    this.action = action;
    this.details = details;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public AuditAction getAction() {
    return action;
  }

  public void setAction(AuditAction action) {
    this.action = action;
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
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

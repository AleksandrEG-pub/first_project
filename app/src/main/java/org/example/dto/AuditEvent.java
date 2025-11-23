package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.model.AuditAction;

@AllArgsConstructor
@Getter
public class AuditEvent {
  private AuditAction auditAction;
  private String details;
}

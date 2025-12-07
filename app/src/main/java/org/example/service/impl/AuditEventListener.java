package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.AuditEvent;
import org.example.service.AuditService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditEventListener {
  private final AuditService auditService;

  @EventListener
  public void listen(AuditEvent auditEvent) {
    auditService.logAction(
        UserContext::getValidatedCurrentUser, auditEvent.getAuditAction(), auditEvent.getDetails());
  }
}

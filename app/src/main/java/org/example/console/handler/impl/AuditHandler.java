package org.example.console.handler.impl;

import java.util.List;
import org.example.console.ui.ConsoleUI;
import org.example.model.AuditLog;
import org.example.service.AuditService;

public class AuditHandler {
  private final ConsoleUI consoleUI;
  private final AuditService auditService;

  public AuditHandler(ConsoleUI consoleUI, AuditService auditService) {
    this.consoleUI = consoleUI;
    this.auditService = auditService;
  }

  public void handleViewAllAuditLogs() {
    List<AuditLog> allLogs = auditService.findAll();
    consoleUI.displayAuditLogs(allLogs);
  }

  public void handleViewAuditLogsByUsername() {
    String username = consoleUI.readString("Enter username to filter audit logs: ");
    List<AuditLog> logs = auditService.findByUsername(username);
    consoleUI.displayAuditLogs(logs);
  }
}

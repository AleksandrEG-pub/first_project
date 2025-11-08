package org.example.console.handler;

import java.util.List;
import org.example.console.ConsoleUI;
import org.example.repository.AuditRepository;

public class AuditHandler {
  private final ConsoleUI consoleUI;
  private final AuditRepository auditRepository;

  public AuditHandler(ConsoleUI consoleUI, AuditRepository auditRepository) {
    this.consoleUI = consoleUI;
    this.auditRepository = auditRepository;
  }

  public void handleViewAllAuditLogs() {
    List<org.example.model.AuditLog> allLogs = auditRepository.findAll();
    consoleUI.displayAuditLogs(allLogs);
    consoleUI.pressEnterToContinue();
  }

  public void handleViewAuditLogsByUsername() {
    String username = consoleUI.readString("Enter username to filter audit logs: ");
    List<org.example.model.AuditLog> logs = auditRepository.findByUsername(username);
    consoleUI.displayAuditLogs(logs);
    consoleUI.pressEnterToContinue();
  }
}

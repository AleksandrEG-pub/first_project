package org.example.console.menu;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.example.console.ui.ConsoleUI;
import org.example.console.handler.AuditHandler;
import org.example.exception.AccessDeniedException;
import org.example.service.AuthService;

public class AuditLogMenu {
  private static final String INVALID_OPTION_MESSAGE = "Invalid option. Please try again.";
  private static final String SELECT_OPTION_MESSAGE = "Select an option: ";
  private static final String BACK_TO_MAIN_MENU_MESSAGE = "Back to Main Menu";

  private final ConsoleUI consoleUI;
  private final AuditHandler auditHandler;
  private final AuthService authService;

  public AuditLogMenu(ConsoleUI consoleUI, AuditHandler auditHandler, AuthService authService) {
    this.consoleUI = consoleUI;
    this.auditHandler = auditHandler;
    this.authService = authService;
  }

  public void show() {
    // Require ADMIN role to view audit logs
    try {
      authService.requireAdmin();
    } catch (AccessDeniedException e) {
      consoleUI.printError(e.getMessage());
      return;
    }

    LinkedHashMap<String, MenuHandler> options = new LinkedHashMap<>();
    options.put("View All Audit Logs", auditHandler::handleViewAllAuditLogs);
    options.put("View Audit Logs by Username", auditHandler::handleViewAuditLogsByUsername);
    options.put(BACK_TO_MAIN_MENU_MESSAGE, () -> {}); // Back to main menu - no action

    consoleUI.printMenu("Audit Log (Admin Only)", new ArrayList<>(options.keySet()));
    int choice = consoleUI.readInt(SELECT_OPTION_MESSAGE);

    List<MenuHandler> handlers = new ArrayList<>(options.values());
    if (choice >= 1 && choice <= handlers.size()) {
      handlers.get(choice - 1).handle();
    } else {
      consoleUI.printError(INVALID_OPTION_MESSAGE);
    }
  }
}

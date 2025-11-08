package org.example.console.menu;

import java.util.HashMap;
import java.util.Map;
import org.example.console.ConsoleUI;
import org.example.console.MenuHandler;
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
      consoleUI.pressEnterToContinue();
      return;
    }

    String[] options = {
      "View All Audit Logs",
      "View Audit Logs by Username",
      BACK_TO_MAIN_MENU_MESSAGE
    };
    Map<Integer, MenuHandler> handlers = new HashMap<>();
    handlers.put(1, auditHandler::handleViewAllAuditLogs);
    handlers.put(2, auditHandler::handleViewAuditLogsByUsername);
    handlers.put(3, () -> {}); // Back to main menu - no action

    consoleUI.printMenu("Audit Log (Admin Only)", options);
    int choice = consoleUI.readInt(SELECT_OPTION_MESSAGE);

    MenuHandler handler = handlers.get(choice);
    if (handler != null) {
      handler.handle();
    } else {
      consoleUI.printError(INVALID_OPTION_MESSAGE);
    }
  }
}


package org.example.console.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.console.ConsoleUI;
import org.example.console.MenuHandler;
import org.example.service.AuthService;

public class MainMenu {
  private static final String INVALID_OPTION_MESSAGE = "Invalid option. Please try again.";
  private static final String SELECT_OPTION_MESSAGE = "Select an option: ";

  private final ConsoleUI consoleUI;
  private final AuthService authService;
  private final ProductManagementMenu productManagementMenu;
  private final SearchFilterMenu searchFilterMenu;
  private final AuditLogMenu auditLogMenu;

  public MainMenu(
      ConsoleUI consoleUI,
      AuthService authService,
      ProductManagementMenu productManagementMenu,
      SearchFilterMenu searchFilterMenu,
      AuditLogMenu auditLogMenu) {
    this.consoleUI = consoleUI;
    this.authService = authService;
    this.productManagementMenu = productManagementMenu;
    this.searchFilterMenu = searchFilterMenu;
    this.auditLogMenu = auditLogMenu;
  }

  public void show() {
    // Build menu options and handlers based on user role
    List<String> optionsList = new ArrayList<>();
    Map<Integer, MenuHandler> handlers = new HashMap<>();

    int optionNumber = 1;
    optionsList.add("Product Management");
    handlers.put(optionNumber++, productManagementMenu::show);

    optionsList.add("Search & Filter");
    handlers.put(optionNumber++, searchFilterMenu::show);

    if (authService.isAdmin()) {
      optionsList.add("View Audit Log");
      handlers.put(optionNumber++, auditLogMenu::show);
    }

    optionsList.add("Logout");
    handlers.put(optionNumber, this::handleLogout);

    String[] options = optionsList.toArray(new String[0]);
    consoleUI.printMenu("Product Catalog - Main Menu", options);
    int choice = consoleUI.readInt(SELECT_OPTION_MESSAGE);

    MenuHandler handler = handlers.get(choice);
    if (handler != null) {
      handler.handle();
    } else {
      consoleUI.printError(INVALID_OPTION_MESSAGE);
    }
  }

  private void handleLogout() {
    authService.logout();
    consoleUI.printMessage("Logged out successfully.");
    consoleUI.pressEnterToContinue();
  }
}


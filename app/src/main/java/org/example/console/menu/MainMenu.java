package org.example.console.menu;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
    LinkedHashMap<String, MenuHandler> options = new LinkedHashMap<>();

    options.put("Product Management", productManagementMenu::show);
    options.put("Search & Filter", searchFilterMenu::show);

    if (authService.isAdmin()) {
      options.put("View Audit Log", auditLogMenu::show);
    }

    options.put("Logout", this::handleLogout);

    consoleUI.printMenu("Product Catalog - Main Menu", new ArrayList<>(options.keySet()));
    int choice = consoleUI.readInt(SELECT_OPTION_MESSAGE);

    List<MenuHandler> handlers = new ArrayList<>(options.values());
    if (choice >= 1 && choice <= handlers.size()) {
      handlers.get(choice - 1).handle();
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

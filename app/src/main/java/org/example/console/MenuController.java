package org.example.console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.console.handler.AuditHandler;
import org.example.console.handler.ProductHandler;
import org.example.console.handler.SearchHandler;
import org.example.exception.AccessDeniedException;
import org.example.repository.AuditRepository;
import org.example.service.AuthService;
import org.example.service.ProductService;

public class MenuController {
  private static final String INVALID_OPTION_MESSAGE = "Invalid option. Please try again.";
  private static final String SELECT_OPTION_MESSAGE = "Select an option: ";
  private static final String BACK_TO_MAIN_MENU_MESSAGE = "Back to Main Menu";
  private final ConsoleUI consoleUI;
  private final AuthService authService;
  private final ProductHandler productHandler;
  private final SearchHandler searchHandler;
  private final AuditHandler auditHandler;
  private boolean running;

  public MenuController(
      ConsoleUI consoleUI,
      AuthService authService,
      ProductService productService,
      AuditRepository auditRepository) {
    this.consoleUI = consoleUI;
    this.authService = authService;
    this.productHandler = new ProductHandler(consoleUI, productService);
    this.searchHandler = new SearchHandler(consoleUI, productService);
    this.auditHandler = new AuditHandler(consoleUI, auditRepository);
    this.running = true;
  }

  public void start() {
    while (running) {
      if (!authService.isAuthenticated()) {
        showLoginMenu();
      } else {
        showMainMenu();
      }
    }
  }

  private void showLoginMenu() {
    String[] options = {"Login", "Exit"};
    consoleUI.printMenu("Product Catalog - Login", options);
    int choice = consoleUI.readInt(SELECT_OPTION_MESSAGE);

    switch (choice) {
      case 1:
        handleLogin();
        break;
      case 2:
        running = false;
        consoleUI.printMessage("Goodbye!");
        break;
      default:
        consoleUI.printError(INVALID_OPTION_MESSAGE);
    }
  }

  private void showMainMenu() {
    // Build menu options and handlers based on user role
    List<String> optionsList = new ArrayList<>();
    Map<Integer, MenuHandler> handlers = new HashMap<>();

    int optionNumber = 1;
    optionsList.add("Product Management");
    handlers.put(optionNumber++, this::showProductManagementMenu);

    optionsList.add("Search & Filter");
    handlers.put(optionNumber++, this::showSearchFilterMenu);

    if (authService.isAdmin()) {
      optionsList.add("View Audit Log");
      handlers.put(optionNumber++, this::showAuditLogMenu);
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

  private void handleLogin() {
    String username = consoleUI.readString("Enter username: ");
    String password = consoleUI.readString("Enter password: ");

    if (authService.login(username, password)) {
      String roleInfo = authService.isAdmin() ? " (Admin)" : "";
      consoleUI.printMessage("Login successful! Welcome, " + username + roleInfo + ".");
      consoleUI.pressEnterToContinue();
    } else {
      consoleUI.printError("Login failed. Invalid username or password, or account may be locked.");
      consoleUI.pressEnterToContinue();
    }
  }

  private void showProductManagementMenu() {
    String[] options = {
      "Add Product",
      "Edit Product",
      "Delete Product",
      "View All Products",
      "Clear Cache",
      BACK_TO_MAIN_MENU_MESSAGE
    };
    Map<Integer, MenuHandler> handlers = new HashMap<>();
    handlers.put(1, productHandler::handleAddProduct);
    handlers.put(2, productHandler::handleEditProduct);
    handlers.put(3, productHandler::handleDeleteProduct);
    handlers.put(4, productHandler::handleViewAllProducts);
    handlers.put(5, productHandler::handleClearCache);
    handlers.put(6, () -> {}); // Back to main menu - no action

    consoleUI.printMenu("Product Management", options);
    int choice = consoleUI.readInt(SELECT_OPTION_MESSAGE);

    MenuHandler handler = handlers.get(choice);
    if (handler != null) {
      handler.handle();
    } else {
      consoleUI.printError(INVALID_OPTION_MESSAGE);
    }
  }

  private void showSearchFilterMenu() {
    String[] options = {
      "Search by Name",
      "Filter by Category",
      "Filter by Brand",
      "Filter by Price Range",
      "Combined Filters",
      BACK_TO_MAIN_MENU_MESSAGE
    };
    Map<Integer, MenuHandler> handlers = new HashMap<>();
    handlers.put(1, searchHandler::handleSearchByName);
    handlers.put(2, searchHandler::handleFilterByCategory);
    handlers.put(3, searchHandler::handleFilterByBrand);
    handlers.put(4, searchHandler::handleFilterByPriceRange);
    handlers.put(5, searchHandler::handleCombinedFilters);
    handlers.put(6, () -> {}); // Back to main menu - no action

    consoleUI.printMenu("Search & Filter", options);
    int choice = consoleUI.readInt(SELECT_OPTION_MESSAGE);

    MenuHandler handler = handlers.get(choice);
    if (handler != null) {
      handler.handle();
    } else {
      consoleUI.printError(INVALID_OPTION_MESSAGE);
    }
  }

  private void showAuditLogMenu() {
    // Require ADMIN role to view audit logs
    try {
      authService.requireAdmin();
    } catch (AccessDeniedException e) {
      consoleUI.printError(e.getMessage());
      consoleUI.pressEnterToContinue();
      return;
    }

    String[] options = {"View All Audit Logs", BACK_TO_MAIN_MENU_MESSAGE};
    Map<Integer, MenuHandler> handlers = new HashMap<>();
    handlers.put(1, auditHandler::handleViewAllAuditLogs);
    handlers.put(2, () -> {}); // Back to main menu - no action

    consoleUI.printMenu("Audit Log (Admin Only)", options);
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

package org.example.console.menu;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.example.console.ui.ConsoleUI;
import org.example.console.handler.ProductHandler;
import org.example.service.AuthService;

public class ProductManagementMenu {
  private static final String INVALID_OPTION_MESSAGE = "Invalid option. Please try again.";
  private static final String SELECT_OPTION_MESSAGE = "Select an option: ";
  private static final String BACK_TO_MAIN_MENU_MESSAGE = "Back to Main Menu";

  private final ConsoleUI consoleUI;
  private final ProductHandler productHandler;
  private final AuthService authService;

  public ProductManagementMenu(ConsoleUI consoleUI, ProductHandler productHandler, AuthService authService) {
    this.consoleUI = consoleUI;
    this.productHandler = productHandler;
    this.authService = authService;
  }

  public void show() {
    Map<String, MenuHandler> options = new LinkedHashMap<>();

    if (authService.isAdmin()) {
      options.put("Clear product cache", productHandler::handleClearCache);
      options.put("Add Product", productHandler::handleAddProduct);
      options.put("Edit Product", productHandler::handleEditProduct);
      options.put("Delete Product", productHandler::handleDeleteProduct);
    }

    // All users can view products
    options.put("View All Products", productHandler::handleViewAllProducts);
    options.put(BACK_TO_MAIN_MENU_MESSAGE, () -> {}); // Back to main menu - no action

    List<String> optionLabels = new ArrayList<>(options.keySet());
    consoleUI.printMenu("Product Management", optionLabels);
    int choice = consoleUI.readInt(SELECT_OPTION_MESSAGE);

    List<MenuHandler> handlers = new ArrayList<>(options.values());
    if (choice >= 1 && choice <= handlers.size()) {
      handlers.get(choice - 1).handle();
    } else {
      consoleUI.printError(INVALID_OPTION_MESSAGE);
    }
  }
}

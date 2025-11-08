package org.example.console.menu;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.example.console.ConsoleUI;
import org.example.console.MenuHandler;
import org.example.console.handler.ProductHandler;

public class ProductManagementMenu {
  private static final String INVALID_OPTION_MESSAGE = "Invalid option. Please try again.";
  private static final String SELECT_OPTION_MESSAGE = "Select an option: ";
  private static final String BACK_TO_MAIN_MENU_MESSAGE = "Back to Main Menu";

  private final ConsoleUI consoleUI;
  private final ProductHandler productHandler;

  public ProductManagementMenu(ConsoleUI consoleUI, ProductHandler productHandler) {
    this.consoleUI = consoleUI;
    this.productHandler = productHandler;
  }

  public void show() {
    Map<String, MenuHandler> options = new LinkedHashMap<>();
    options.put("Add Product", productHandler::handleAddProduct);
    options.put("Edit Product", productHandler::handleEditProduct);
    options.put("Delete Product", productHandler::handleDeleteProduct);
    options.put("View All Products", productHandler::handleViewAllProducts);
    options.put("Clear Cache", productHandler::handleClearCache);
    options.put(BACK_TO_MAIN_MENU_MESSAGE, () -> {}); // Back to main menu - no action

    consoleUI.printMenu("Product Management", new ArrayList<>(options.keySet()));
    int choice = consoleUI.readInt(SELECT_OPTION_MESSAGE);

    List<MenuHandler> handlers = new ArrayList<>(options.values());
    if (choice >= 1 && choice <= handlers.size()) {
      handlers.get(choice - 1).handle();
    } else {
      consoleUI.printError(INVALID_OPTION_MESSAGE);
    }
  }
}

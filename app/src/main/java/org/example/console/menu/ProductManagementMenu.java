package org.example.console.menu;

import java.util.HashMap;
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
}


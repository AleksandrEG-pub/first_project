package org.example.console.menu;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.example.console.ui.ConsoleUI;
import org.example.console.handler.LoginHandler;

public class LoginMenu {
  private static final String INVALID_OPTION_MESSAGE = "Invalid option. Please try again.";
  private static final String SELECT_OPTION_MESSAGE = "Select an option: ";

  private final ConsoleUI consoleUI;
  private final LoginHandler loginHandler;
  private boolean shouldExit;

  public LoginMenu(ConsoleUI consoleUI, LoginHandler loginHandler) {
    this.consoleUI = consoleUI;
    this.loginHandler = loginHandler;
    this.shouldExit = false;
  }

  public void show() {
    LinkedHashMap<String, MenuHandler> options = new LinkedHashMap<>();
    options.put("Login", loginHandler::handleLogin);
    options.put("Exit", () -> {
      shouldExit = true;
      consoleUI.printMessage("Goodbye!");
    });

    consoleUI.printMenu("Product Catalog - Login", new ArrayList<>(options.keySet()));
    int choice = consoleUI.readInt(SELECT_OPTION_MESSAGE);

    List<MenuHandler> handlers = new ArrayList<>(options.values());
    if (choice >= 1 && choice <= handlers.size()) {
      handlers.get(choice - 1).handle();
    } else {
      consoleUI.printError(INVALID_OPTION_MESSAGE);
    }
  }

  public boolean shouldExit() {
    return shouldExit;
  }
}

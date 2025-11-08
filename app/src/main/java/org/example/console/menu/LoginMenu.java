package org.example.console.menu;

import org.example.console.ConsoleUI;
import org.example.console.handler.LoginHandler;
import org.example.service.AuthService;

public class LoginMenu {
  private static final String INVALID_OPTION_MESSAGE = "Invalid option. Please try again.";
  private static final String SELECT_OPTION_MESSAGE = "Select an option: ";
  
  private final ConsoleUI consoleUI;
  private final LoginHandler loginHandler;
  private boolean shouldExit;

  public LoginMenu(ConsoleUI consoleUI, LoginHandler loginHandler, AuthService authService) {
    this.consoleUI = consoleUI;
    this.loginHandler = loginHandler;
    this.authService = authService;
    this.shouldExit = false;
  }

  public void show() {
    String[] options = {"Login", "Exit"};
    consoleUI.printMenu("Product Catalog - Login", options);
    int choice = consoleUI.readInt(SELECT_OPTION_MESSAGE);

    switch (choice) {
      case 1:
        loginHandler.handleLogin();
        break;
      case 2:
        shouldExit = true;
        consoleUI.printMessage("Goodbye!");
        break;
      default:
        consoleUI.printError(INVALID_OPTION_MESSAGE);
    }
  }

  public boolean shouldExit() {
    return shouldExit;
  }
}


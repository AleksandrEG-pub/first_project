package org.example.console.handler;

import org.example.console.ConsoleUI;
import org.example.service.AuthService;

public class LoginHandler {
  private final ConsoleUI consoleUI;
  private final AuthService authService;

  public LoginHandler(ConsoleUI consoleUI, AuthService authService) {
    this.consoleUI = consoleUI;
    this.authService = authService;
  }

  public void handleLogin() {
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
}


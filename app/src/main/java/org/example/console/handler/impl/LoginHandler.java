package org.example.console.handler.impl;

import org.example.console.ui.ConsoleUI;
import org.example.dto.LoginResult;
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

    LoginResult loginResult = authService.login(username, password);
    if (loginResult.isSuccess()) {
      String roleInfo = authService.isAdmin() ? " (Admin)" : "";
      consoleUI.printMessage("Login successful! Welcome, " + username + roleInfo + ".");
    } else {
      consoleUI.printError(loginResult.getMessage());
    }
  }
}

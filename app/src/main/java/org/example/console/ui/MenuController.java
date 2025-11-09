package org.example.console.ui;

import org.example.exception.UserExitException;
import org.example.console.menu.LoginMenu;
import org.example.console.menu.MainMenu;
import org.example.service.AuthService;

public class MenuController {
  private final ConsoleUI consoleUI;
  private final AuthService authService;
  private final LoginMenu loginMenu;
  private final MainMenu mainMenu;

  public MenuController(
      ConsoleUI consoleUI, AuthService authService, LoginMenu loginMenu, MainMenu mainMenu) {
    this.consoleUI = consoleUI;
    this.authService = authService;
    this.loginMenu = loginMenu;
    this.mainMenu = mainMenu;
  }

  public void start() {
    try {
      while (true) {
        if (!authService.isAuthenticated()) {
          loginMenu.show();
          if (loginMenu.shouldExit()) {
            break;
          }
        } else {
          mainMenu.show();
        }
      }
    } catch (UserExitException e) {
      consoleUI.printMessage("Exiting application (input closed or user requested exit).");
    } catch (RuntimeException e) {
      consoleUI.printError("Unexpected error: " + e.getMessage());
    }
  }
}

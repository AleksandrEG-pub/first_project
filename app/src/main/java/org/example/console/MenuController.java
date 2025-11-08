package org.example.console;

import org.example.console.handler.AuditHandler;
import org.example.console.handler.LoginHandler;
import org.example.console.handler.ProductHandler;
import org.example.console.handler.SearchHandler;
import org.example.console.menu.AuditLogMenu;
import org.example.console.menu.LoginMenu;
import org.example.console.menu.MainMenu;
import org.example.console.menu.ProductManagementMenu;
import org.example.console.menu.SearchFilterMenu;
import org.example.repository.AuditRepository;
import org.example.service.AuthService;
import org.example.service.ProductService;

public class MenuController {
  private final AuthService authService;
  private final LoginMenu loginMenu;
  private final MainMenu mainMenu;

  public MenuController(
      ConsoleUI consoleUI,
      AuthService authService,
      ProductService productService,
      AuditRepository auditRepository) {
    this.consoleUI = consoleUI;
    this.authService = authService;

    // Initialize handlers
    LoginHandler loginHandler = new LoginHandler(consoleUI, authService);
    ProductHandler productHandler = new ProductHandler(consoleUI, productService);
    SearchHandler searchHandler = new SearchHandler(consoleUI, productService);
    AuditHandler auditHandler = new AuditHandler(consoleUI, auditRepository);

    // Initialize menus
    this.loginMenu = new LoginMenu(consoleUI, loginHandler, authService);
    ProductManagementMenu productManagementMenu =
        new ProductManagementMenu(consoleUI, productHandler);
    SearchFilterMenu searchFilterMenu = new SearchFilterMenu(consoleUI, searchHandler);
    AuditLogMenu auditLogMenu = new AuditLogMenu(consoleUI, auditHandler, authService);
    this.mainMenu =
        new MainMenu(consoleUI, authService, productManagementMenu, searchFilterMenu, auditLogMenu);
  }

  public void start() {
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
  }
}

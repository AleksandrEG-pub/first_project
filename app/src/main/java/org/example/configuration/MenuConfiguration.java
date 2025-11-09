package org.example.configuration;

import org.example.console.menu.AuditLogMenu;
import org.example.console.menu.LoginMenu;
import org.example.console.menu.MainMenu;
import org.example.console.menu.ProductManagementMenu;
import org.example.console.menu.SearchFilterMenu;
import org.example.console.ui.MenuController;

public class MenuConfiguration {
  private final MenuController menuController;

  public MenuConfiguration(
      ServiceConfiguration services, UIConfiguration ui, HandlerConfiguration handlers) {
    AuditLogMenu auditLogMenu =
        new AuditLogMenu(ui.getConsoleUI(), handlers.getAuditHandler(), services.getAuthService());
    SearchFilterMenu searchFilterMenu =
        new SearchFilterMenu(ui.getConsoleUI(), handlers.getSearchHandler());
    ProductManagementMenu productManagementMenu =
        new ProductManagementMenu(
            ui.getConsoleUI(), handlers.getProductHandler(), services.getAuthService());
    LoginMenu loginMenu = new LoginMenu(ui.getConsoleUI(), handlers.getLoginHandler());
    MainMenu mainMenu =
        new MainMenu(
            ui.getConsoleUI(),
            services.getAuthService(),
            productManagementMenu,
            searchFilterMenu,
            auditLogMenu);

    this.menuController =
        new MenuController(ui.getConsoleUI(), services.getAuthService(), loginMenu, mainMenu);
  }

  public MenuController getMenuController() {
    return menuController;
  }
}

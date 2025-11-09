package org.example.configuration;

import java.util.List;
import org.example.exception.UserExitException;
import org.example.model.Product;
import org.example.util.DataInitializer;

public class ApplicationConfiguration {
  private final ServiceConfiguration services;
  private final UIConfiguration ui;
  private final HandlerConfiguration handlers;
  private final MenuConfiguration menus;

  public ApplicationConfiguration() {
    this.ui = new UIConfiguration();
    this.services = new FileServiceConfiguration(ui.getConsoleUI());
    this.handlers = new HandlerConfiguration(services, ui);
    this.menus = new MenuConfiguration(services, ui, handlers);
  }

  public UIConfiguration getUi() {
    return ui;
  }

  public void initializeData() {
    List<Product> allProducts = services.getProductService().getAllProducts();
    if (allProducts.isEmpty()) {
      DataInitializer.initializeDefaultData(
          services.getUserRepository(), services.getProductService());
    }
  }

  public void registerShutdownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
  }

  public void shutdown() {
    try {
      ui.getConsoleUI().printMessage("Shutting down...");
    } catch (Exception ignored) {
      // quit application
    }
    try {
      ui.getConsoleUI().close();
    } catch (Exception ignored) {
      // quit application
    }
  }

  public void start() throws UserExitException {
    menus.getMenuController().start();
  }
}

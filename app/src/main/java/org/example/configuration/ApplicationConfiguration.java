package org.example.configuration;

import java.util.List;
import org.example.model.Product;
import org.example.util.DataInitializer;

/**
 * Central application configuration and lifecycle coordinator.
 *
 * <p>Constructs the required service, UI and handler configurations (either file-backed or
 * in-memory), provides lifecycle operations such as data initialization, startup and shutdown,
 * and registers a JVM shutdown hook.
 */
public class ApplicationConfiguration {
  private final ServiceConfiguration services;
  private final UIConfiguration ui;
  private final HandlerConfiguration handlers;
  private final MenuConfiguration menus;

  /**
   * Create a new application configuration.
   *
   * @param inMemory if true, configure services to run in-memory (no file persistence)
   */
  public ApplicationConfiguration(boolean inMemory) {
    this.ui = new UIConfiguration();
    if (inMemory) {
      this.services = new InMemoryServiceConfiguration();
    } else {
      this.services = new FileServiceConfiguration(ui.getConsoleUI());
    }
    this.handlers = new HandlerConfiguration(services, ui);
    this.menus = new MenuConfiguration(services, ui, handlers);
  }

  public UIConfiguration getUi() {
    return ui;
  }

  /**
   * Initialize default data when the persistent store is empty.
   */
  public void initializeData() {
    List<Product> allProducts = services.getProductService().getAllProducts();
    if (allProducts.isEmpty()) {
      DataInitializer.initializeDefaultData(
          services.getUserRepository(), services.getProductService());
    }
  }

  /**
   * Attempt to gracefully shutdown the application components (UI and other closeable resources).
   * This method intentionally swallows exceptions because it's called during shutdown.
   */
  public void shutdown() {
    try {
      ui.getConsoleUI().printMessage("Shutting down...");
    } catch (Exception ignored) {
      // quit application
    }
  }

  /**
   * Start the application's main menu loop.
   */
  public void start() {
    menus.getMenuController().start();
  }
}

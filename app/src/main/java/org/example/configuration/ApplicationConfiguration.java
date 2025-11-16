package org.example.configuration;

import java.util.List;
import org.example.model.Product;
import org.example.repository.impl.database.ConnectionManager;
import org.example.repository.impl.file.FileProductRepository;
import org.example.service.impl.DataInitializerImpl;

/**
 * Central application configuration and lifecycle coordinator.
 *
 * <p>Constructs the required service, UI and handler configurations (either file-backed or
 * in-memory), provides lifecycle operations such as data initialization, startup and shutdown, and
 * registers a JVM shutdown hook.
 */
public class ApplicationConfiguration {
  private final ServiceConfiguration services;
  private final UIConfiguration ui;
  private final MenuConfiguration menus;

  public ApplicationConfiguration(RepositoryType repositoryType) {
    this.ui = new UIConfiguration();
    DatabaseProperties databaseProperties = new EnvDatabaseProperties();
    ConnectionManager connectionManager = new ConnectionManager(databaseProperties);
    LiquibaseConfiguration liquibaseConfiguration = new LiquibaseConfiguration.Builder().fromEnvironment().build();
    switch (repositoryType) {
      case IN_MEMORY -> this.services = new InMemoryServiceConfiguration();
      case FILE -> this.services = new FileServiceConfiguration(ui.getConsoleUI());
      case DATABASE ->
          this.services = new DatabaseServiceConfiguration(ui.getConsoleUI(), connectionManager, liquibaseConfiguration);
      default ->
          throw new IllegalArgumentException("Unsupported repository type: " + repositoryType);
    }
    HandlerConfiguration handlers = new HandlerConfiguration(services, ui);
    this.menus = new MenuConfiguration(services, ui, handlers);
  }

  public UIConfiguration getUi() {
    return ui;
  }

  public void initializeData() {
    if (services instanceof FileServiceConfiguration) {
      List<Product> allProducts = services.getProductService().getAllProducts();
      if (allProducts.isEmpty()) {
        new DataInitializerImpl(
                services.getUserRepository(),
                services.getProductService(),
                services.getAuthService())
            .initializeDefaultData();
      } else {
        allProducts.stream()
            .mapToLong(Product::getId)
            .max()
            .ifPresent(maxId -> FileProductRepository.updateCounter(maxId + 1));
      }
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

  /** Start the application's main menu loop. */
  public void start() {
    menus.getMenuController().start();
  }
}

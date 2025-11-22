package org.example.configuration;

import org.example.repository.impl.database.ConnectionManager;

/**
 * Central application configuration and lifecycle coordinator.
 *
 * <p>Constructs the required service, UI and handler configurations (either file-backed or
 * in-memory), provides lifecycle operations such as data initialization, startup and shutdown, and
 * registers a JVM shutdown hook.
 */
public class ApplicationConfiguration {
  private final UIConfiguration ui;
  private final MenuConfiguration menus;

  public ApplicationConfiguration() {
    DatabaseProperties databaseProperties = new EnvDatabaseProperties();
    ConnectionManager connectionManager = new ConnectionManager(databaseProperties);
    ServiceConfiguration services = new DatabaseServiceConfiguration(connectionManager);

    this.ui = new UIConfiguration();
    HandlerConfiguration handlers = new HandlerConfiguration(services, ui);
    this.menus = new MenuConfiguration(services, ui, handlers);
  }

  public UIConfiguration getUi() {
    return ui;
  }

  public void initializeData() {
    LiquibaseConfiguration liquibaseConfiguration =
        new LiquibaseConfiguration.Builder().fromEnvironment().build();
    new LiquibaseConfigurationUpdater(ui.getConsoleUI(), liquibaseConfiguration)
        .runDatabaseUpdate("production");
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

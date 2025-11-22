package org.example.configuration;

import org.apache.catalina.LifecycleException;
import org.example.exception.ApplicationException;
import org.example.repository.impl.database.ConnectionManager;
import org.example.web.configuration.EnvironmentServerConfigurationProperties;
import org.example.web.configuration.ServerConfiguration;
import org.example.web.configuration.ServletMapping;
import org.example.web.configuration.ServletMappingImpl;

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
  private final ServiceConfiguration services;

  public ApplicationConfiguration() {
    DatabaseProperties databaseProperties = new EnvDatabaseProperties();
    ConnectionManager connectionManager = new ConnectionManager(databaseProperties);
    services = new DatabaseServiceConfiguration(connectionManager);

    this.ui = new UIConfiguration();
    HandlerConfiguration handlers = new HandlerConfiguration(services, ui);
    this.menus = new MenuConfiguration(services, ui, handlers);
  }

  public UIConfiguration getUi() {
    return ui;
  }

  public ServiceConfiguration getServices() {
    return services;
  }

  public void initializeData() {
    LiquibaseConfiguration liquibaseConfiguration =
        new LiquibaseConfiguration.Builder().fromEnvironment().build();
    new LiquibaseConfigurationUpdater(ui.getConsoleUI(), liquibaseConfiguration)
        .runDatabaseUpdate("production");
  }

  public void startServer(ServiceConfiguration services) {
    EnvironmentServerConfigurationProperties serverConfigurationProperties = new EnvironmentServerConfigurationProperties();
    ServletMapping servletMapping = new ServletMappingImpl(services.getProductService());
    ServerConfiguration serverConfiguration = new ServerConfiguration(serverConfigurationProperties,
            servletMapping);
      try {
          serverConfiguration.startServer();
      } catch (LifecycleException e) {
          throw new ApplicationException(e);
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

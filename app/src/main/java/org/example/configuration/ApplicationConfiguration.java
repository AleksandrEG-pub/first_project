package org.example.configuration;

import lombok.Getter;
import org.apache.catalina.LifecycleException;
import org.example.exception.ApplicationException;
import org.example.repository.impl.database.ConnectionManager;
import org.example.web.configuration.impl.EnvironmentServerConfigurationProperties;
import org.example.web.configuration.ServerConfiguration;
import org.example.web.configuration.ServletMapping;
import org.example.web.configuration.impl.ServletMappingImpl;

/** Central application configuration and lifecycle coordinator. */
@Getter
public class ApplicationConfiguration {
  private final ServiceConfiguration services;

  public ApplicationConfiguration() {
    DatabaseProperties databaseProperties = new EnvDatabaseProperties();
    ConnectionManager connectionManager = new ConnectionManager(databaseProperties);
    services = new DatabaseServiceConfiguration(connectionManager);
  }

  public void initializeData() {
    LiquibaseConfiguration liquibaseConfiguration =
        new LiquibaseConfiguration.Builder().fromEnvironment().build();
    new LiquibaseConfigurationUpdater(liquibaseConfiguration).runDatabaseUpdate("production");
  }

  public void startServer(ServiceConfiguration services) {
    EnvironmentServerConfigurationProperties serverConfigurationProperties =
        new EnvironmentServerConfigurationProperties();
    ServletMapping servletMapping =
        new ServletMappingImpl(
            services.getAuditService(),
            services.getProductService(),
            services.getDtoValidator(),
            services.getObjectMapper());
    ServerConfiguration serverConfiguration =
        new ServerConfiguration(
            services.getAuthService(),
            serverConfigurationProperties,
            servletMapping,
            services.getObjectMapper());
    try {
      serverConfiguration.startServer();
    } catch (LifecycleException e) {
      throw new ApplicationException(e);
    }
  }
}

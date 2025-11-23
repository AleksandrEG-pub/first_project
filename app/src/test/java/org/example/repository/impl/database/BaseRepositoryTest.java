package org.example.repository.impl.database;

import org.example.configuration.DatabaseProperties;
import org.example.configuration.LiquibaseConfiguration;
import org.example.configuration.LiquibaseConfigurationUpdater;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BaseRepositoryTest {

  @Container
  private final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:17.5");

  ConnectionManager connectionManager;

  @BeforeEach
  void setUp() {
    String appScheme = "test_app";
    String jdbcUrl = postgreSQLContainer.getJdbcUrl() + "&currentSchema=" + appScheme;
    String username = postgreSQLContainer.getUsername();
    String password = postgreSQLContainer.getPassword();
    DatabaseProperties databaseProperties = getDatabaseProperties(jdbcUrl, username, password);
    connectionManager = new ConnectionManager(databaseProperties);
    LiquibaseConfiguration liquibaseConfiguration =
        new LiquibaseConfiguration.Builder()
            .withUrl(databaseProperties.getUrl())
            .withUsername(databaseProperties.getUser())
            .withPassword(databaseProperties.getPassword())
            .withApplicationScheme(appScheme)
            .withLiquibaseScheme("test_liquibase")
            .withChangelogFile("db/changelog/db.changelog-master.yaml")
            .build();
    new LiquibaseConfigurationUpdater(liquibaseConfiguration).runDatabaseUpdate("test");
    setupBeforeEach();
  }

  private DatabaseProperties getDatabaseProperties(
      String jdbcUrl, String username, String password) {
    return new DatabaseProperties() {
      @Override
      public String getUrl() {
        return jdbcUrl;
      }

      @Override
      public String getUser() {
        return username;
      }

      @Override
      public String getPassword() {
        return password;
      }
    };
  }

  abstract void setupBeforeEach();
}

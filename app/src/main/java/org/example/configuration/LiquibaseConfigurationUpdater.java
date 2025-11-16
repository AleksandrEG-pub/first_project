package org.example.configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.example.console.ui.ConsoleUI;
import org.example.exception.InitializationException;

public class LiquibaseConfigurationUpdater {

  private final ConsoleUI consoleUI;
  private final LiquibaseConfiguration liquibaseConfiguration;

  public LiquibaseConfigurationUpdater(
      ConsoleUI consoleUI, LiquibaseConfiguration liquibaseConfiguration) {
      this.consoleUI = consoleUI;
      this.liquibaseConfiguration = liquibaseConfiguration;
  }

  public void runDatabaseUpdate() {
    try (Connection connection =
        DriverManager.getConnection(
            liquibaseConfiguration.getUrl(),
            liquibaseConfiguration.getUsername(),
            liquibaseConfiguration.getPassword())) {
      createSchemas(connection);
      Database database =
          DatabaseFactory.getInstance()
              .findCorrectDatabaseImplementation(new JdbcConnection(connection));
      database.setLiquibaseSchemaName(liquibaseConfiguration.getLiquibaseScheme());
      database.setDefaultSchemaName(liquibaseConfiguration.getApplicationScheme());
      Liquibase liquibase =
          new Liquibase(
              liquibaseConfiguration.getChangelogFile(),
              new ClassLoaderResourceAccessor(),
              database);
      liquibase.update();
    } catch (LiquibaseException | SQLException e) {
      consoleUI.printError("Failed to update database: " + e.getMessage());
      throw new InitializationException(e);
    }
  }

  private void createSchemas(Connection connection) throws SQLException {
    try (Statement stmt = connection.createStatement()) {
      stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + liquibaseConfiguration.getLiquibaseScheme());
    }
    try (Statement stmt = connection.createStatement()) {
      stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + liquibaseConfiguration.getApplicationScheme());
    }
  }
}

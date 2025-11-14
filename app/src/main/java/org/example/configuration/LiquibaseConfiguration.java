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

public class LiquibaseConfiguration {

  private static final String CHANGELOG_FILE = "db/changelog/db.changelog-master.yaml";
  private static final String URL = System.getenv("YLAB_PROJECT_POSTGRES_URL");
  private static final String USER = System.getenv("YLAB_PROJECT_POSTGRES_USER");
  private static final String PASSWORD = System.getenv("YLAB_PROJECT_POSTGRES_PASSWORD");
  private static final String LIQUIBASE_SCHEME = System.getenv("YLAB_PROJECT_LIQUIBASE_SCHEME");
  private static final String APPLICATION_SCHEME = System.getenv("YLAB_PROJECT_APPLICATION_SCHEME");

  private final ConsoleUI consoleUI;

  public LiquibaseConfiguration(ConsoleUI consoleUI) {
    this.consoleUI = consoleUI;
  }

  public void runDatabaseUpdate() {
    if (URL == null || USER == null || PASSWORD == null) {
      throw new IllegalStateException("Database configuration environment variables are not set");
    }
    validateScheme(LIQUIBASE_SCHEME);
    validateScheme(APPLICATION_SCHEME);
    consoleUI.printMessage("Connecting to database: " + URL);
    try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
      createSchemas(connection);
      Database database =
          DatabaseFactory.getInstance()
              .findCorrectDatabaseImplementation(new JdbcConnection(connection));
      database.setLiquibaseSchemaName(LIQUIBASE_SCHEME);
      database.setDefaultSchemaName(APPLICATION_SCHEME);
      Liquibase liquibase =
          new Liquibase(CHANGELOG_FILE, new ClassLoaderResourceAccessor(), database);
      liquibase.update();
      consoleUI.printMessage("Database updated successfully");
    } catch (LiquibaseException | SQLException e) {
      consoleUI.printError("Failed to update database: " + e.getMessage());
      throw new InitializationException(e);
    }
  }

  private static void validateScheme(String scheme) {
    if (!scheme.matches("[a-zA-Z_]{0,20}")) {
      throw new IllegalArgumentException("Invalid schema name");
    }
  }

  private void createSchemas(Connection connection) throws SQLException {
    try (Statement stmt = connection.createStatement()) {
      stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + LIQUIBASE_SCHEME);
    }
    try (Statement stmt = connection.createStatement()) {
      stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + APPLICATION_SCHEME);
    }
  }
}

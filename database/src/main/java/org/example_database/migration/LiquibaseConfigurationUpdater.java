package org.example_database.migration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import jakarta.annotation.PostConstruct;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.example_database.exception.InitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LiquibaseConfigurationUpdater {

  private static final Set<String> validSchemes =
      Set.of("liquibase_data", "application_data", "app_data");

  /** liquibase configuration file location */
  @Value("${spring.liquibase.change-log}")
  private String changelogFile;

  /** url of database to connect */
  @Value("${spring.datasource.url}")
  private String url;

  /** user to connect to database */
  @Value("${spring.datasource.username}")
  private String username;

  /** password to connect to database */
  @Value("${spring.datasource.password}")
  private String password;

  /** name of database scheme with liquibase tables */
  @Value("${spring.liquibase.liquibase-schema}")
  private String liquibaseScheme;

  /** name of database scheme with application tables and data */
  @Value("${spring.liquibase.default-schema}")
  private String applicationScheme;

  @PostConstruct
  private void init() {
    runDatabaseUpdate("test");
  }

  public void runDatabaseUpdate(String context) {
    try (Connection connection = DriverManager.getConnection(url, username, password)) {
      createSchemas(connection);
      Database database =
          DatabaseFactory.getInstance()
              .findCorrectDatabaseImplementation(new JdbcConnection(connection));
      validateSchemes(liquibaseScheme);
      validateSchemes(applicationScheme);
      connection.getMetaData().getIdentifierQuoteString();
      database.setLiquibaseSchemaName(liquibaseScheme);
      database.setDefaultSchemaName(applicationScheme);
      Liquibase liquibase =
          new Liquibase(changelogFile, new ClassLoaderResourceAccessor(), database);
      liquibase.update(context);
    } catch (LiquibaseException | SQLException e) {
      log.error("Failed to update database: {}", e.getMessage(), e);
      throw new InitializationException(e);
    }
  }

  private void validateSchemes(String scheme) {
    if (!validSchemes.contains(scheme)) {
      throw new InitializationException("Invalid schema name format");
    }
  }

  private void createSchemas(Connection connection) throws SQLException {
    try (Statement stmt = connection.createStatement()) {
      stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + liquibaseScheme);
    }
    try (Statement stmt = connection.createStatement()) {
      stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + applicationScheme);
    }
  }
}

package org.example.configuration;

import org.example.console.ui.ConsoleUI;

public class DatabaseServiceConfiguration extends ServiceConfiguration {
  public DatabaseServiceConfiguration(ConsoleUI consoleUI) {
    super(
        new DatabaseProductRepository(consoleUI),
        new DatabaseUserRepository(consoleUI),
        new DatabaseAuditRepository(consoleUI));
    new LiquibaseConfiguration(consoleUI).runDatabaseUpdate();
  }
}

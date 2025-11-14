package org.example.configuration;

import org.example.console.ui.ConsoleUI;
import org.example.repository.impl.database.DatabaseAuditRepository;
import org.example.repository.impl.database.DatabaseProductRepository;
import org.example.repository.impl.database.DatabaseUserRepository;

public class DatabaseServiceConfiguration extends ServiceConfiguration {
  public DatabaseServiceConfiguration(ConsoleUI consoleUI) {
    super(
        new DatabaseProductRepository(consoleUI),
        new DatabaseUserRepository(consoleUI),
        new DatabaseAuditRepository(consoleUI));
    new LiquibaseConfiguration(consoleUI).runDatabaseUpdate();
  }
}

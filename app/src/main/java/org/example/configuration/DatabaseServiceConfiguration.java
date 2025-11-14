package org.example.configuration;

import org.example.console.ui.ConsoleUI;
import org.example.repository.impl.database.ConnectionManager;
import org.example.repository.impl.database.JdbcAuditRepository;
import org.example.repository.impl.database.JdbcProductRepository;
import org.example.repository.impl.database.JdbcUserRepository;

public class DatabaseServiceConfiguration extends ServiceConfiguration {
  public DatabaseServiceConfiguration(ConsoleUI consoleUI, ConnectionManager connectionManager) {
    super(
        new JdbcProductRepository(connectionManager),
        new JdbcUserRepository(connectionManager),
        new JdbcAuditRepository(connectionManager));
    new LiquibaseConfiguration(consoleUI).runDatabaseUpdate();
  }
}

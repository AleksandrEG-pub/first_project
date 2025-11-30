package org.example.repository.impl.database;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.SQLException;
import java.sql.Statement;

@Testcontainers
public abstract class BaseRepositoryTest {

  @Container
  static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:17.5");

  @DynamicPropertySource
  static void applicationProperties(DynamicPropertyRegistry registry) {
    registry.add("database.connect.url", postgreSQLContainer::getJdbcUrl);
    registry.add("database.connect.user", postgreSQLContainer::getUsername);
    registry.add("database.connect.password", postgreSQLContainer::getPassword);
    registry.add("database.connect.application_scheme", () -> "test_app");
    registry.add("database.migration.liquibase.scheme", () -> "test_liq");
    registry.add("database.migration.liquibase.changelog_file", () -> "db/changelog/db.changelog-master.yaml");
  }

  @Autowired
  ConnectionManager connectionManager;

  @AfterEach
  void clear() {
    connectionManager.doInTransaction(connection -> {
      try {
        Statement statement = connection.createStatement();
        statement.executeUpdate("delete from audit_logs");
        statement.executeUpdate("delete from products");
        statement.executeUpdate("delete from users");
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
      return null;
    });
  }
}

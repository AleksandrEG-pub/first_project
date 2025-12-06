package org.example.repository.impl.database;

import java.sql.SQLException;
import java.sql.Statement;
import org.example_database.database.ConnectionManager;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BaseRepositoryTest {

  @Container
  static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:17.5");

  @Autowired ConnectionManager connectionManager;

  @DynamicPropertySource
  static void applicationProperties(DynamicPropertyRegistry registry) {
    String appSchema = "application_data";
    String jdbcUrl = postgreSQLContainer.getJdbcUrl();
    String url =
        jdbcUrl.contains("?")
            ? jdbcUrl + "&currentSchema=" + appSchema
            : jdbcUrl + "?currentSchema=" + appSchema;
    registry.add("spring.datasource.url", () -> url);
    registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    registry.add("spring.liquibase.liquibase-schema", () -> "liquibase_data");
    registry.add("spring.liquibase.default-schema", () -> "application_data");
    registry.add("spring.liquibase.change-log", () -> "db/changelog/db.changelog-master.yaml");
  }

  @AfterEach
  void clear() {
    connectionManager.doInTransaction(
        connection -> {
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

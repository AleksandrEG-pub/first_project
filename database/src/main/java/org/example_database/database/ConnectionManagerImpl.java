package org.example_database.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Function;
import org.example_database.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Value;

public class ConnectionManagerImpl implements ConnectionManager {

  @Value("${spring.datasource.url}")
  private String url;

  @Value("${spring.datasource.username}")
  private String user;

  @Value("${spring.datasource.password}")
  private String password;

  @Override
  public <T> T doInTransaction(Function<Connection, T> connectionFunction) {
    try (Connection connection = getConnection()) {
      connection.setAutoCommit(false);
      return invokeWithConnection(connectionFunction, connection);
    } catch (SQLException e) {
      throw new DataAccessException("Connection operation failed", e);
    }
  }

  private Connection getConnection() throws SQLException {
    return DriverManager.getConnection(url, user, password);
  }

  private <T> T invokeWithConnection(
      Function<Connection, T> connectionFunction, Connection connection) {
    try {
      T result = connectionFunction.apply(connection);
      connection.commit();
      return result;
    } catch (SQLException e) {
      tryRollback(e, connection);
      return null;
    }
  }

  private void tryRollback(SQLException e, Connection connection) {
    try {
      connection.rollback();
    } catch (SQLException rollbackEx) {
      throw new DataAccessException("Rollback failed", rollbackEx);
    }
    throw new DataAccessException("Transaction failed", e);
  }
}

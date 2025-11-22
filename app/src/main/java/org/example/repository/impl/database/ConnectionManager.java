package org.example.repository.impl.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Function;
import org.example.configuration.DatabaseProperties;
import org.example.exception.DataAccessException;

public class ConnectionManager {
  private final DatabaseProperties properties;

  public ConnectionManager(DatabaseProperties prop) {
    this.properties = prop;
  }

  public <T> T doInTransaction(Function<Connection, T> connectionFunction) {
    try (Connection connection = getConnection()) {
      connection.setAutoCommit(false);
      return invokeWithConnection(connectionFunction, connection);
    } catch (SQLException e) {
      throw new DataAccessException("Connection operation failed", e);
    }
  }

  private Connection getConnection() throws SQLException {
    return DriverManager.getConnection(
        properties.getUrl(), properties.getUser(), properties.getPassword());
  }

  private <T> T invokeWithConnection(Function<Connection, T> connectionFunction, Connection connection) {
    try {
      T result = connectionFunction.apply(connection);
      connection.commit();
      return result;
    } catch (SQLException e) {
      tryRollback(e, connection);
      return null;
    }
  }

  private static void tryRollback(SQLException e, Connection connection) {
    try {
      connection.rollback();
    } catch (SQLException rollbackEx) {
      throw new DataAccessException("Rollback failed", rollbackEx);
    }
    throw new DataAccessException("Transaction failed", e);
  }
}

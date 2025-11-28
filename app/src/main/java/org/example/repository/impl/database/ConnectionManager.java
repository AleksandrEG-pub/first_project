package org.example.repository.impl.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Function;
import org.example.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConnectionManager {

  @Value("${database.connect.url}")
  private String url;

  @Value("${database.connect.user}")
  private String user;

  @Value("${database.connect.password}")
  private String password;

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

  private static void tryRollback(SQLException e, Connection connection) {
    try {
      connection.rollback();
    } catch (SQLException rollbackEx) {
      throw new DataAccessException("Rollback failed", rollbackEx);
    }
    throw new DataAccessException("Transaction failed", e);
  }
}

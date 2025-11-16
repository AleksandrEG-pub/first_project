package org.example.repository.impl.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.example.exception.DataAccessException;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.repository.impl.database.mapper.UserResultMapper;

public class JdbcUserRepository implements UserRepository {

  private static final String FIND_BY_USERNAME_SQL =
      """
        SELECT id, username, password_hash, role
        FROM users WHERE username = ?
        """;

  private static final String INSERT_SQL =
      """
        INSERT INTO users (username, password_hash, role)
        VALUES (?, ?, ?)
        """;

  private static final String UPDATE_SQL =
      """
        UPDATE users SET username = ?, password_hash = ?, role = ?
        WHERE id = ?
        """;
  private final ConnectionManager connectionManager;
  private final UserResultMapper userResultMapper = new UserResultMapper();

  public JdbcUserRepository(ConnectionManager connectionManager) {
    this.connectionManager = connectionManager;
  }

  @Override
  public User findByUsername(String username) {
    return connectionManager.doInTransaction(
        connection -> {
          try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_USERNAME_SQL)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
              if (rs.next()) {
                return userResultMapper.mapToUser(rs);
              } else {
                return null;
              }
            }
          } catch (SQLException e) {
            throw new DataAccessException("Failed to find user by username: " + username, e);
          }
        });
  }

  @Override
  public User save(User user) {
    return connectionManager.doInTransaction(
        connection -> {
          if (user.getId() == null) {
            return insertUser(connection, user);
          } else {
            return updateUser(connection, user);
          }
        });
  }

  private User insertUser(Connection connection, User user) {
    try (PreparedStatement stmt =
        connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, user.getUsername());
      stmt.setString(2, user.getPasswordHash());
      stmt.setString(3, user.getRole().name());

      int affectedRows = stmt.executeUpdate();
      if (affectedRows == 0) {
        throw new DataAccessException("Creating user failed, no rows affected.");
      }

      try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          user.setId(generatedKeys.getLong(1));
        } else {
          throw new DataAccessException("Creating user failed, no ID obtained.");
        }
      }

      return user;
    } catch (SQLException e) {
      throw new DataAccessException("Failed to insert user", e);
    }
  }

  private User updateUser(Connection connection, User user) {
    try (PreparedStatement stmt = connection.prepareStatement(UPDATE_SQL)) {
      stmt.setString(1, user.getUsername());
      stmt.setString(2, user.getPasswordHash());
      stmt.setString(3, user.getRole().name());
      stmt.setLong(4, user.getId());

      int affectedRows = stmt.executeUpdate();
      if (affectedRows == 0) {
        throw new DataAccessException("Updating user failed, no rows affected.");
      }

      return user;
    } catch (SQLException e) {
      throw new DataAccessException("Failed to update user", e);
    }
  }
}

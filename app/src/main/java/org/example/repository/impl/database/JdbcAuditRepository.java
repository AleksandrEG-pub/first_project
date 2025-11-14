package org.example.repository.impl.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.example.exception.DataAccessException;
import org.example.model.AuditLog;
import org.example.repository.AuditRepository;

public class JdbcAuditRepository implements AuditRepository {
  private static final String INSERT_SQL =
      """
        INSERT INTO audit_logs (timestamp, username, action, details)
        VALUES (?, ?, ?, ?)
        """;
  private static final String FIND_BY_USERNAME_SQL =
      """
        SELECT id, timestamp, username, action, details
        FROM audit_logs WHERE username = ? ORDER BY timestamp DESC
        """;
  private static final String FIND_ALL_SQL =
      """
        SELECT id, timestamp, username, action, details
        FROM audit_logs ORDER BY timestamp DESC
        """;
  private final ConnectionManager connectionManager;
  private final AuditLogResultMapper auditLogResultMapper = new AuditLogResultMapper();

  public JdbcAuditRepository(ConnectionManager connectionManager) {
    this.connectionManager = connectionManager;
  }

  @Override
  public AuditLog save(AuditLog auditLog) {
    return connectionManager.doInTransaction(
        connection -> {
          try (PreparedStatement stmt =
              connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(auditLog.getTimestamp()));
            stmt.setString(2, auditLog.getUsername());
            stmt.setString(3, auditLog.getAction().name());
            stmt.setString(4, auditLog.getDetails());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
              throw new DataAccessException("Creating audit log failed, no rows affected.");
            }
            AuditLog saved = AuditLog.fromAuditLog(auditLog);
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
              if (generatedKeys.next()) {
                saved.setId(generatedKeys.getLong(1));
              } else {
                throw new DataAccessException("Creating audit log failed, no ID obtained.");
              }
            }
            return saved;
          } catch (SQLException e) {
            throw new DataAccessException("Failed to save audit log", e);
          }
        });
  }

  @Override
  public List<AuditLog> findByUsername(String username) {
    return connectionManager.doInTransaction(
        connection -> {
          List<AuditLog> logs = new ArrayList<>();
          try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_USERNAME_SQL)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
              while (rs.next()) {
                logs.add(auditLogResultMapper.mapToAuditLog(rs));
              }
            }
            return logs;
          } catch (SQLException e) {
            throw new DataAccessException("Failed to find audit logs by username: " + username, e);
          }
        });
  }

  @Override
  public List<AuditLog> findAll() {
    return connectionManager.doInTransaction(
        connection -> {
          List<AuditLog> logs = new ArrayList<>();
          try (PreparedStatement stmt = connection.prepareStatement(FIND_ALL_SQL);
              ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
              logs.add(auditLogResultMapper.mapToAuditLog(rs));
            }
            return logs;
          } catch (SQLException e) {
            throw new DataAccessException("Failed to find all audit logs", e);
          }
        });
  }
}

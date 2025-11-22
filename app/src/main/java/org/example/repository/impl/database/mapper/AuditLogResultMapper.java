package org.example.repository.impl.database.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.example.model.AuditAction;
import org.example.model.AuditLog;

public class AuditLogResultMapper {
  public AuditLog mapToAuditLog(ResultSet rs) throws SQLException {
    AuditLog log = new AuditLog();
    log.setId(rs.getLong("id"));
    log.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
    log.setUsername(rs.getString("username"));
    log.setAction(AuditAction.valueOf(rs.getString("action")));
    log.setDetails(rs.getString("details"));
    return log;
  }
}

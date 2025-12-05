package org.example_audit.repository.impl.database.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.example_audit.model.AuditAction;
import org.example_audit.model.AuditLog;

public class AuditLogResultMapper {
  public AuditLog mapToAuditLog(ResultSet rs) throws SQLException {
    AuditLog log = new AuditLog();
    log.setId(rs.getLong("id"));
    log.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
    log.setUsername(rs.getString("username"));
    log.setAction(AuditAction.valueOf(rs.getString("action")));
    log.setDetails(rs.getString("details"));
    log.setResource(rs.getString("resource"));
    return log;
  }
}

package org.example.repository.impl.file;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.example.console.ui.ConsoleUI;
import org.example.model.AuditAction;
import org.example.model.AuditLog;
import org.example.repository.AuditRepository;

public class FileAuditRepository extends BaseFileRepository implements AuditRepository {
  private static final AtomicLong counter = new AtomicLong(0);
  public FileAuditRepository(ConsoleUI ui, File file) {
    super(ui, file);
  }

  @Override
  public AuditLog save(AuditLog auditLog) {
    if (auditLog == null) {
      throw new IllegalArgumentException("AuditLog cannot be null");
    }
    if (auditLog.getId() == null) {
      auditLog.setId(counter.getAndIncrement());
    }
    return executeWithMetrics(
        () -> {
          List<String> lines = readAllLines();
          lines.add(toCsvLine(auditLog));
          writeAllLines(lines);
          return auditLog;
        });
  }

  private String toCsvLine(AuditLog log) {
    return String.join(
        ",",
        CsvUtils.escapeCsv(log.getTimestamp() != null ? log.getTimestamp().toString() : ""),
        CsvUtils.escapeCsv(log.getUsername()),
        CsvUtils.escapeCsv(log.getAction() != null ? log.getAction().name() : ""),
        CsvUtils.escapeCsv(log.getDetails()));
  }

  @Override
  public List<AuditLog> findByUsername(String username) {
    return executeWithMetrics(
        () -> {
          if (username == null) return new ArrayList<>();
          return readAllLines().stream()
              .filter(line -> !line.trim().isEmpty())
              .map(this::parseCsvLine)
              .filter(log -> username.equals(log.getUsername()))
              .toList();
        });
  }

  private AuditLog parseCsvLine(String line) {
    String[] cols = CsvUtils.splitCsv(line);
    if (cols.length < 4) return new AuditLog();

    AuditLog log = new AuditLog();
    log.setId(parseId(cols[0]));
    log.setTimestamp(parseTimestamp(cols[1]));
    log.setUsername(cols[2]);
    log.setAction(parseAction(cols[3]));
    log.setDetails(cols[4]);
    return log;
  }

  private Long parseId(String idStr) {
    if (idStr == null || idStr.isEmpty()) {
      return null;
    }
    try {
      return Long.parseLong(idStr);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private LocalDateTime parseTimestamp(String timestampStr) {
    if (timestampStr == null || timestampStr.isEmpty()) {
      return null;
    }
    try {
      return LocalDateTime.parse(timestampStr);
    } catch (DateTimeParseException e) {
      return null;
    }
  }

  private AuditAction parseAction(String actionStr) {
    if (actionStr == null || actionStr.isEmpty()) return null;
    try {
      return AuditAction.valueOf(actionStr);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  @Override
  public List<AuditLog> findAll() {
    return executeWithMetrics(
        () ->
            readAllLines().stream()
                .filter(line -> !line.trim().isEmpty())
                .map(this::parseCsvLine)
                .toList());
  }
}

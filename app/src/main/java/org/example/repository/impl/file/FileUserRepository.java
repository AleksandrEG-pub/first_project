package org.example.repository.impl.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.example.console.ui.ConsoleUI;
import org.example.exception.InitializationException;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.UserRepository;

public class FileUserRepository extends BaseFileRepository implements UserRepository {
  private static final AtomicLong counter = new AtomicLong(0);

  public FileUserRepository(ConsoleUI ui, File file) {
    super(ui, file);
  }

  @Override
  public User findByUsername(String username) {
    return executeWithMetrics(
        () -> {
          if (username == null) return null;

          return readAllLines().stream()
              .filter(line -> !line.trim().isEmpty())
              .map(this::parseCsvLine)
              .filter(user -> username.equals(user.getUsername()))
              .findFirst()
              .orElse(null);
        });
  }

  private User parseCsvLine(String line) {
    String[] cols = CsvUtils.splitCsv(line);
    if (cols.length != 4) {
      throw new InitializationException("incorrect format of data file, must be 4 columns");
    }

    User user = new User();
    user.setId(Long.parseLong(cols[0]));
    user.setUsername(cols[1]);
    user.setPasswordHash(cols[2]);
    user.setRole(parseRole(cols[3]));
    return user;
  }

  private Role parseRole(String roleStr) {
    if (roleStr == null || roleStr.isEmpty()) {
      return null;
    }
    try {
      return Role.valueOf(roleStr);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  @Override
  public User save(User user) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }
    if (user.getId() == null) {
      user.setId(counter.getAndIncrement());
    }
    return executeWithMetrics(
        () -> {
          List<String> lines = readAllLines();
          List<String> newLines = new ArrayList<>();
          boolean updated = false;

          for (String line : lines) {
            if (line.trim().isEmpty()) {
              continue;
            }

            User existing = parseCsvLine(line);
            if (user.getId().equals(existing.getId())) {
              newLines.add(toCsvLine(user));
              updated = true;
            } else {
              newLines.add(line);
            }
          }

          if (!updated) {
            newLines.add(toCsvLine(user));
          }

          writeAllLines(newLines);
          return user;
        });
  }

  private String toCsvLine(User user) {
    return String.join(
        ",",
        CsvUtils.escapeCsv(user.getId().toString()),
        CsvUtils.escapeCsv(user.getUsername()),
        CsvUtils.escapeCsv(user.getPasswordHash()),
        CsvUtils.escapeCsv(user.getRole() == null ? "" : user.getRole().name()));
  }
}

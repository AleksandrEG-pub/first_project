package org.example.repository.impl.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.example.console.ui.ConsoleUI;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.UserRepository;

public class FileUserRepository extends BaseFileRepository implements UserRepository {

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
    if (cols.length < 3) return new User();

    User user = new User();
    user.setUsername(cols[0]);
    user.setPasswordHash(cols[1]);
    user.setRole(parseRole(cols[2]));
    return user;
  }

  private Role parseRole(String roleStr) {
    if (roleStr == null || roleStr.isEmpty()) return null;
    try {
      return Role.valueOf(roleStr);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  @Override
  public User save(User user) {
    return executeWithMetrics(
        () -> {
          if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
          }

          List<String> lines = readAllLines();
          List<String> newLines = new ArrayList<>();
          boolean updated = false;

          for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            User existing = parseCsvLine(line);
            if (user.getUsername().equals(existing.getUsername())) {
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
        CsvUtils.escapeCsv(user.getUsername()),
        CsvUtils.escapeCsv(user.getPasswordHash()),
        CsvUtils.escapeCsv(user.getRole() == null ? "" : user.getRole().name()));
  }
}

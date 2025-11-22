package org.example.repository.impl.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.example.console.ui.ConsoleUI;
import org.example.exception.DataAccessException;

public abstract class BaseFileRepository {
  protected final File file;
  private final ConsoleUI ui;
  private long queryCount = 0;
  private long totalQueryTimeNs = 0;

  protected BaseFileRepository(ConsoleUI ui, File file) {
    this.ui = ui;
    this.file = file;
  }

  protected final synchronized <T> T executeWithMetrics(Supplier<T> operation) {
    long start = System.nanoTime();
    try {
      return operation.get();
    } finally {
      recordQuery(start);
    }
  }

  private void recordQuery(long startNs) {
    long duration = System.nanoTime() - startNs;
    queryCount++;
    totalQueryTimeNs += duration;
    String message =
        "Query executed in %d ns. Total queries: %d. Average time: %s ns."
            .formatted(duration, queryCount, getAverageQueryTimeNs());
    ui.printMessage(message);
  }

  public synchronized double getAverageQueryTimeNs() {
    return queryCount == 0 ? 0.0 : (double) totalQueryTimeNs / queryCount;
  }

  protected final List<String> readAllLines() {
    if (!file.exists()) {
      return new ArrayList<>();
    }

    try {
      CsvUtils.ensureParentDirectoryExists(file);
      return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new DataAccessException("Failed to read from file: " + file.getAbsolutePath(), e);
    }
  }

  protected final void writeAllLines(List<String> lines) {
    try {
      CsvUtils.ensureParentDirectoryExists(file);
      Files.write(
          file.toPath(),
          lines,
          StandardCharsets.UTF_8,
          StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new DataAccessException("Failed to write to file: " + file.getAbsolutePath(), e);
    }
  }
}

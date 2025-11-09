package org.example.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvUtils {
  private static final char DELIMITER = ',';
  private static final char QUOTE_CHAR = '"';

  private CsvUtils() {}

  public static String[] splitCsv(String line) {
    if (line == null || line.isEmpty()) {
      return new String[0];
    }

    List<String> columns = new ArrayList<>();
    StringBuilder currentColumn = new StringBuilder();
    boolean inQuotes = false;

    for (int i = 0; i < line.length(); i++) {
      char currentChar = line.charAt(i);

      if (inQuotes) {
        inQuotes = handleCharacterInsideQuotes(line, currentChar, i, currentColumn);
      } else {
        inQuotes = handleCharacterOutsideQuotes(currentChar, currentColumn, columns);
      }
    }

    columns.add(currentColumn.toString());
    return columns.toArray(new String[0]);
  }

  /**
   * Handle character when inside quoted section
   *
   * @return true if still in quotes, false if quotes ended
   */
  private static boolean handleCharacterInsideQuotes(
      String line, char currentChar, int position, StringBuilder currentColumn) {
    if (currentChar == QUOTE_CHAR) {
      if (isDoubleQuote(line, position)) {
        currentColumn.append(QUOTE_CHAR);
        return true; // Still in quotes (escaped quote)
      } else {
        return false; // Quotes ended
      }
    } else {
      currentColumn.append(currentChar);
      return true; // Still in quotes
    }
  }

  /**
   * Handle character when outside quoted section
   *
   * @return true if entered quotes, false otherwise
   */
  private static boolean handleCharacterOutsideQuotes(
      char currentChar, StringBuilder currentColumn, List<String> columns) {
    switch (currentChar) {
      case DELIMITER:
        columns.add(currentColumn.toString());
        currentColumn.setLength(0);
        return false; // Not entering quotes

      case QUOTE_CHAR:
        return true; // Entering quotes

      default:
        currentColumn.append(currentChar);
        return false; // Not entering quotes
    }
  }

  private static boolean isDoubleQuote(String line, int position) {
    return position + 1 < line.length() && line.charAt(position + 1) == QUOTE_CHAR;
  }

  public static String escapeCsv(String value) {
    if (value == null) return "";

    boolean needsQuotes =
        value.indexOf(DELIMITER) >= 0
            || value.indexOf('\n') >= 0
            || value.indexOf('\r') >= 0
            || value.indexOf(QUOTE_CHAR) >= 0;

    if (!needsQuotes) {
      return value;
    }

    return "\"" + value.replace("\"", "\"\"") + "\"";
  }

  public static void ensureParentDirectoryExists(File file) throws IOException {
    if (file == null) return;

    File parent = file.getParentFile();
    if (parent != null && !parent.exists() && !parent.mkdirs()) {
      throw new IOException("Failed to create directory: " + parent.getAbsolutePath());
    }
  }
}

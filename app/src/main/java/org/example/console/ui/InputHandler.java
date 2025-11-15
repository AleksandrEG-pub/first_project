package org.example.console.ui;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.example.exception.UserExitException;

public class InputHandler {
  private final ConsoleIO io;

  public InputHandler(ConsoleIO consoleIO) {
    this.io = consoleIO;
  }

  public int readInt(String prompt) {
    while (true) {
      try {
        String input = io.readString(prompt);
        return Integer.parseInt(input);
      } catch (NumberFormatException e) {
        io.printError("Invalid input. Please enter a number.");
      } catch (NoSuchElementException e) {
        throw new UserExitException("Input closed", e);
      }
    }
  }

  public long readLong(String prompt) {
    while (true) {
      try {
        String input = io.readString(prompt);
        return Long.parseLong(input);
      } catch (NumberFormatException e) {
        io.printError("Invalid input. Please enter a number.");
      } catch (NoSuchElementException e) {
        throw new UserExitException("Input closed", e);
      }
    }
  }

  public Optional<BigDecimal> readBigDecimal(String prompt) {
    String input = readString(prompt);
    try {
      return input.isEmpty() ? Optional.empty() : Optional.of(new BigDecimal(input));
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid number format: " + input);
    }
  }

  public String readString(String prompt) {
    return io.readString(prompt).trim();
  }
}

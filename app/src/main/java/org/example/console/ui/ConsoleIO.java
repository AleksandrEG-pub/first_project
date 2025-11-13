package org.example.console.ui;

import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;
import org.example.exception.UserExitException;

public class ConsoleIO {
  private final Scanner scanner;
  private final PrintStream out;

  public ConsoleIO() {
    this(new Scanner(System.in), System.out);
  }

  public ConsoleIO(Scanner scanner, PrintStream out) {
    this.scanner = scanner;
    this.out = out;
  }

  public String readString(String prompt) {
    out.print(prompt);
    try {
      return scanner.nextLine();
    } catch (NoSuchElementException e) {
      throw new UserExitException("Input closed", e);
    }
  }

  public void printSeparator() {
    out.println("----------------------------------------");
  }

  public void printMessage(String message) {
    out.println(message);
  }

  public void printError(String error) {
    out.println("ERROR: " + error);
  }

  public void close() {
    scanner.close();
  }
}

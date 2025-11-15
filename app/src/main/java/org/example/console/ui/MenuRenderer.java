package org.example.console.ui;

import java.util.List;

public class MenuRenderer {
  private final ConsoleIO io;
  private final DisplayFormatter formatter;

  public MenuRenderer(ConsoleIO consoleIO, DisplayFormatter formatter) {
    this.io = consoleIO;
    this.formatter = formatter;
  }

  public void renderMenu(String title, List<String> options) {
    io.printSeparator();
    io.printMessage(title);
    io.printSeparator();
    String formattedOptions = formatter.formatMenuOption(options);
    io.printMessage(formattedOptions);
    io.printSeparator();
  }
}

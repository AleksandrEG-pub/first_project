package org.example.configuration;

import org.example.console.ui.ConsoleUIImpl;
import org.example.console.ui.MenuRenderer;
import org.example.console.ui.ConsoleIO;
import org.example.console.ui.ConsoleUI;
import org.example.console.ui.DisplayFormatter;
import org.example.console.ui.InputHandler;
import org.example.console.ui.ProductInputHandler;

public class UIConfiguration {
  private final ConsoleUI consoleUI;

  public UIConfiguration() {
    ConsoleIO consoleIO = new ConsoleIO();
    DisplayFormatter displayFormatter = new DisplayFormatter();
    MenuRenderer menuRenderer = new MenuRenderer(consoleIO, displayFormatter);
    InputHandler inputHandler = new InputHandler(consoleIO);
    ProductInputHandler productInputHandler = new ProductInputHandler(inputHandler, consoleIO);
    this.consoleUI =
        new ConsoleUIImpl(consoleIO, inputHandler, displayFormatter, menuRenderer, productInputHandler);
  }

  public ConsoleUI getConsoleUI() {
    return consoleUI;
  }
}

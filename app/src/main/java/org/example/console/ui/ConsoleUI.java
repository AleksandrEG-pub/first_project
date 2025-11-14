package org.example.console.ui;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.example.console.menu.MenuRenderer;
import org.example.model.AuditLog;
import org.example.model.Product;

public class ConsoleUI {
  private final ConsoleIO io;
  private final InputHandler inputHandler;
  private final DisplayFormatter displayFormatter;
  private final MenuRenderer menuRenderer;
  private final ProductInputHandler productInputHandler;

  public ConsoleUI(
      ConsoleIO io,
      InputHandler inputHandler,
      DisplayFormatter displayFormatter,
      MenuRenderer menuRenderer,
      ProductInputHandler productInputHandler) {
    this.io = io;
    this.inputHandler = inputHandler;
    this.displayFormatter = displayFormatter;
    this.menuRenderer = menuRenderer;
    this.productInputHandler = productInputHandler;
  }

  public void displayProduct(Product product) {
    if (product == null) {
      printMessage("Product not found.");
      return;
    }
    io.printSeparator();
    String formattedProduct = displayFormatter.formatProduct(product);
    io.printMessage(formattedProduct);
    io.printSeparator();
  }

  public void printMenu(String title, List<String> options) {
    menuRenderer.renderMenu(title, options);
  }

  public void displayProducts(List<Product> products) {
    if (products == null || products.isEmpty()) {
      printMessage("No products found.");
      return;
    }
    io.printSeparator();
    io.printMessage("Products (" + products.size() + "):");
    io.printSeparator();
    for (String product : displayFormatter.formatProductList(products)) {
      io.printMessage(product);
      io.printSeparator();
    }
  }

  public void displayAuditLogs(List<AuditLog> logs) {
    if (logs == null || logs.isEmpty()) {
      printMessage("No audit logs found.");
      return;
    }
    io.printSeparator();
    io.printMessage("Audit Logs (" + logs.size() + "):");
    io.printSeparator();
    String formattedLogs =
            String.join(System.lineSeparator(), displayFormatter.formatAuditLogs(logs));
    io.printMessage(formattedLogs);
    io.printSeparator();
  }

  public Product readProductDataForUpdate(Product existing) {
    return productInputHandler.readProductDataForUpdate(existing);
  }

  public int readInt(String prompt) {
    return inputHandler.readInt(prompt);
  }

  public long readLong(String prompt) {
    return inputHandler.readLong(prompt);
  }

  public void printMessage(String message) {
    io.printMessage(message);
  }

  public Optional<Product> readProductData() {
    return productInputHandler.readProductData();
  }

  public String readString(String prompt) {
    return inputHandler.readString(prompt);
  }

  public Optional<BigDecimal> readBigDecimal(String prompt) {
    return inputHandler.readBigDecimal(prompt);
  }

  public void printError(String error) {
    io.printError(error);
  }
}

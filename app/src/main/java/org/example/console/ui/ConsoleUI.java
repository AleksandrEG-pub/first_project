package org.example.console.ui;

import org.example.model.AuditLog;
import org.example.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ConsoleUI {
  void displayProduct(Product product);

  void printMessage(String message);

  void printMenu(String title, List<String> options);

  void displayProducts(List<Product> products);

  void displayAuditLogs(List<AuditLog> logs);

  Product readProductDataForUpdate(Product existing);

  int readInt(String prompt);

  long readLong(String prompt);

  Optional<Product> readProductData();

  String readString(String prompt);

  Optional<BigDecimal> readBigDecimal(String prompt);

  void printError(String error);
}

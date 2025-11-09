package org.example.console.ui;

import java.time.format.DateTimeFormatter;
import java.util.List;
import org.example.model.AuditLog;
import org.example.model.Product;

public class DisplayFormatter {
  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public String formatProduct(Product product) {
    return getFormattedProduct(product);
  }

  private String getFormattedProduct(Product product) {
    return String.join(
        System.lineSeparator(),
        List.of(
            "ID: " + product.getId(),
            "Name: " + product.getName(),
            "Category: " + product.getCategory(),
            "Brand: " + product.getBrand(),
            "Price: " + product.getPrice()));
  }

  public List<String> formatProductList(List<Product> products) {
    return products.stream().map(this::getFormattedProduct).toList();
  }

  public List<String> formatAuditLogs(List<AuditLog> logs) {
    return logs.stream().map(this::getFormattedAuditLog).toList();
  }

  private String getFormattedAuditLog(AuditLog log) {
    return String.join(
        System.lineSeparator(),
        List.of(
            "Time: " + log.getTimestamp().format(DATE_FORMATTER),
            "User: " + log.getUsername(),
            "Action: " + log.getAction(),
            "Details: " + log.getDetails()));
  }

  public String formatMenuOption(List<String> options) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < options.size(); i++) {
      sb.append("%d. %s%n".formatted(i + 1, options.get(i)));
    }
    return sb.toString();
  }
}

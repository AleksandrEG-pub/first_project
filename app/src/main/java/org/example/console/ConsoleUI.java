package org.example.console;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import org.example.model.AuditLog;
import org.example.model.Product;

public class ConsoleUI {
  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private static final String PRESS_ENTER_TO_KEEP_MESSAGE = ", press Enter to keep): ";
  private final Scanner scanner;

  public ConsoleUI() {
    this.scanner = new Scanner(System.in);
  }

  public void printMenu(String title, String[] options) {
    printSeparator();
    System.out.println(title);
    printSeparator();
    for (int i = 0; i < options.length; i++) {
      System.out.println((i + 1) + ". " + options[i]);
    }
    printSeparator();
  }

  public void printSeparator() {
    System.out.println("----------------------------------------");
  }

  public int readInt(String prompt) {
    while (true) {
      System.out.print(prompt);
      try {
        String input = scanner.nextLine().trim();
        return Integer.parseInt(input);
      } catch (NumberFormatException e) {
        printError("Invalid input. Please enter a number.");
      }
    }
  }

  public void printError(String error) {
    System.out.println("ERROR: " + error);
  }

  public void displayProduct(Product product) {
    if (product == null) {
      printMessage("Product not found.");
      return;
    }
    printSeparator();
    System.out.println("Product ID: " + product.getId());
    System.out.println("Name: " + product.getName());
    System.out.println("Description: " + product.getDescription());
    System.out.println("Category: " + product.getCategory());
    System.out.println("Brand: " + product.getBrand());
    System.out.println("Price: " + product.getPrice());
    printSeparator();
  }

  public void printMessage(String message) {
    System.out.println(message);
  }

  public void displayProducts(List<Product> products) {
    if (products == null || products.isEmpty()) {
      printMessage("No products found.");
      return;
    }

    printSeparator();
    System.out.println("Products (" + products.size() + "):");
    printSeparator();
    for (Product product : products) {
      System.out.println("ID: " + product.getId());
      System.out.println("Name: " + product.getName());
      System.out.println("Category: " + product.getCategory());
      System.out.println("Brand: " + product.getBrand());
      System.out.println("Price: " + product.getPrice());
      printSeparator();
    }
  }

  public void displayAuditLogs(List<AuditLog> logs) {
    if (logs == null || logs.isEmpty()) {
      printMessage("No audit logs found.");
      return;
    }

    printSeparator();
    System.out.println("Audit Logs (" + logs.size() + "):");
    printSeparator();
    for (AuditLog log : logs) {
      System.out.println("Time: " + log.getTimestamp().format(DATE_FORMATTER));
      System.out.println("User: " + log.getUsername());
      System.out.println("Action: " + log.getAction());
      System.out.println("Details: " + log.getDetails());
      printSeparator();
    }
  }

  public Product readProductData() {
    String name = readString("Enter product name: ");
    String description = readString("Enter product description: ");
    String category = readString("Enter product category: ");
    String brand = readString("Enter product brand: ");
    BigDecimal price = readBigDecimal("Enter product price: ");

    return new Product(null, name, description, category, brand, price);
  }

  public String readString(String prompt) {
    System.out.print(prompt);
    return scanner.nextLine().trim();
  }

  public BigDecimal readBigDecimal(String prompt) {
    while (true) {
      System.out.print(prompt);
      try {
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
          return null;
        }
        return new BigDecimal(input);
      } catch (NumberFormatException e) {
        printError("Invalid input. Please enter a valid decimal number.");
      }
    }
  }
  public Product readProductDataForUpdate(Product existing) {
    String name =
        readString(
                "Enter product name (current: " + existing.getName() + PRESS_ENTER_TO_KEEP_MESSAGE);
    if (name.isEmpty()) {
      name = existing.getName();
    }

    String description =
        readString(
                "Enter product description (current: "
                + existing.getDescription()
                + PRESS_ENTER_TO_KEEP_MESSAGE);
    if (description.isEmpty()) {
      description = existing.getDescription();
    }

    String category =
        readString(
                "Enter product category (current: "
                + existing.getCategory()
                + PRESS_ENTER_TO_KEEP_MESSAGE);
    if (category.isEmpty()) {
      category = existing.getCategory();
    }

    String brand =
        readString(
                "Enter product brand (current: " + existing.getBrand() + PRESS_ENTER_TO_KEEP_MESSAGE);
    if (brand.isEmpty()) {
      brand = existing.getBrand();
    }

    String priceInput =
        readString(
                "Enter product price (current: " + existing.getPrice() + PRESS_ENTER_TO_KEEP_MESSAGE);
    BigDecimal price;
    if (priceInput.isEmpty()) {
      price = existing.getPrice();
    } else {
      try {
        price = new BigDecimal(priceInput);
      } catch (NumberFormatException e) {
        printError("Invalid price format. Keeping current price.");
        price = existing.getPrice();
      }
    }

    return new Product(existing.getId(), name, description, category, brand, price);
  }

  public void pressEnterToContinue() {
    System.out.print("Press Enter to continue...");
    scanner.nextLine();
  }

  public void close() {
    scanner.close();
  }
}

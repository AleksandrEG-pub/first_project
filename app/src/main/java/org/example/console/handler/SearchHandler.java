package org.example.console.handler;

import java.math.BigDecimal;
import java.util.List;
import org.example.console.ConsoleUI;
import org.example.service.ProductService;

public class SearchHandler {
  private final ConsoleUI consoleUI;
  private final ProductService productService;

  public SearchHandler(ConsoleUI consoleUI, ProductService productService) {
    this.consoleUI = consoleUI;
    this.productService = productService;
  }

  public void handleSearchByName() {
    String name = consoleUI.readString("Enter product name to search: ");
    List<org.example.model.Product> results = productService.searchByName(name);
    consoleUI.displayProducts(results);
    consoleUI.pressEnterToContinue();
  }

  public void handleFilterByCategory() {
    String category = consoleUI.readString("Enter category to filter: ");
    List<org.example.model.Product> results = productService.filterByCategory(category);
    consoleUI.displayProducts(results);
    consoleUI.pressEnterToContinue();
  }

  public void handleFilterByBrand() {
    String brand = consoleUI.readString("Enter brand to filter: ");
    List<org.example.model.Product> results = productService.filterByBrand(brand);
    consoleUI.displayProducts(results);
    consoleUI.pressEnterToContinue();
  }

  public void handleFilterByPriceRange() {
    String minInput = consoleUI.readString("Enter minimum price (press Enter to skip): ");
    BigDecimal minPrice = minInput.isEmpty() ? null : new BigDecimal(minInput);

    String maxInput = consoleUI.readString("Enter maximum price (press Enter to skip): ");
    BigDecimal maxPrice = maxInput.isEmpty() ? null : new BigDecimal(maxInput);

    List<org.example.model.Product> results = productService.filterByPriceRange(minPrice, maxPrice);
    consoleUI.displayProducts(results);
    consoleUI.pressEnterToContinue();
  }

  public void handleCombinedFilters() {
    String category = consoleUI.readString("Enter category to filter (press Enter to skip): ");
    if (category.isEmpty()) {
      category = null;
    }

    String brand = consoleUI.readString("Enter brand to filter (press Enter to skip): ");
    if (brand.isEmpty()) {
      brand = null;
    }

    String minInput = consoleUI.readString("Enter minimum price (press Enter to skip): ");
    BigDecimal minPrice = minInput.isEmpty() ? null : new BigDecimal(minInput);

    String maxInput = consoleUI.readString("Enter maximum price (press Enter to skip): ");
    BigDecimal maxPrice = maxInput.isEmpty() ? null : new BigDecimal(maxInput);

    List<org.example.model.Product> results =
        productService.applyCombinedFilters(category, brand, minPrice, maxPrice);
    consoleUI.displayProducts(results);
    consoleUI.pressEnterToContinue();
  }
}

package org.example.console.handler;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.example.console.ui.ConsoleUI;
import org.example.model.Product;
import org.example.service.ProductService;
import org.example.service.SearchCriteria;

public class SearchHandler {
  private final ConsoleUI consoleUI;
  private final ProductService productService;

  public SearchHandler(ConsoleUI consoleUI, ProductService productService) {
    this.consoleUI = consoleUI;
    this.productService = productService;
  }

  public void handleSearchById() {
    Long id = consoleUI.readLong("Enter product id to search): ");
    Optional<Product> productOpt = productService.findById(id);
    if (productOpt.isPresent()) {
      consoleUI.displayProduct(productOpt.get());
    } else {
      consoleUI.printMessage("Product not found for id: " + id);
    }
  }

  public void handleSearchByName() {
    String name = consoleUI.readString("Enter product name to search): ");
    SearchCriteria criteria = new SearchCriteria.Builder().name(name).build();
    findByCriteria(criteria);
  }

  public void handleFilterByCategory() {
    String category = consoleUI.readString("Enter category to filter): ");
    SearchCriteria criteria = new SearchCriteria.Builder().category(category).build();
    findByCriteria(criteria);
  }

  public void handleFilterByBrand() {
    String brand = consoleUI.readString("Enter brand to filter): ");
    SearchCriteria criteria = new SearchCriteria.Builder().brand(brand).build();
    findByCriteria(criteria);
  }

  public void handleFilterByPriceRange() {
    BigDecimal minPrice =
        consoleUI
            .readBigDecimal("Enter minimum price (press Enter to skip): ")
            .orElse(BigDecimal.ZERO);
    BigDecimal maxPrice =
        consoleUI
            .readBigDecimal("Enter maximum price (press Enter to skip): ")
            .orElse(BigDecimal.valueOf(Double.MAX_VALUE));

    SearchCriteria criteria =
        new SearchCriteria.Builder().minPrice(minPrice).maxPrice(maxPrice).build();

    findByCriteria(criteria);
  }

  public void handleCombinedFilters() {
    SearchCriteria.Builder builder = new SearchCriteria.Builder();

    String name = consoleUI.readString("Enter name (press Enter to skip): ");
    builder.name(name);

    String category = consoleUI.readString("Enter category (press Enter to skip): ");
    builder.category(category);

    String brand = consoleUI.readString("Enter brand (press Enter to skip): ");
    builder.brand(brand);

    BigDecimal minPrice =
        consoleUI.readBigDecimal("Enter minimum price (press Enter to skip): ").orElse(BigDecimal.ZERO);
    builder.minPrice(minPrice);

    BigDecimal maxPrice =
        consoleUI.readBigDecimal(
            "Enter maximum price (press Enter to skip): ").orElse(BigDecimal.valueOf(Double.MAX_VALUE));
    builder.maxPrice(maxPrice);

    findByCriteria(builder.build());
  }

  private void findByCriteria(SearchCriteria criteria) {
    try {
      List<Product> results = productService.search(criteria);
      consoleUI.displayProducts(results);
    } catch (Exception e) {
      consoleUI.printError("Error during search: " + e.getMessage());
    }
  }
}

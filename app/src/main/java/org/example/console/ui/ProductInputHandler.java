package org.example.console.ui;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Consumer;
import org.example.model.Product;

public class ProductInputHandler {
  private static final String PRESS_ENTER_TO_KEEP_MESSAGE = ", press Enter to keep): ";
  private final InputHandler inputHandler;
  private final ConsoleIO consoleIO;

  public ProductInputHandler(InputHandler inputHandler, ConsoleIO consoleIO) {
    this.inputHandler = inputHandler;
    this.consoleIO = consoleIO;
  }

  public Optional<Product> readProductData() {
    String name = inputHandler.readString("Enter product name: ");
    String description = inputHandler.readString("Enter product description: ");
    String category = inputHandler.readString("Enter product category: ");
    String brand = inputHandler.readString("Enter product brand: ");
    Optional<BigDecimal> priceOpt = inputHandler.readBigDecimal("Enter product price: ");

    // Ensure price is provided for new product; otherwise caller should handle null
    if (priceOpt.isEmpty()) {
      consoleIO.printError("Price is required for a new product.");
      return Optional.empty();
    }
    if (priceOpt.get().compareTo(BigDecimal.ZERO) < 0) {
      consoleIO.printError("Price cannot be negative.");
      return Optional.empty();
    }
    return Optional.ofNullable(
        Product.builder()
            .name(name)
            .description(description)
            .category(category)
            .brand(brand)
            .price(priceOpt.get())
            .build());
  }

  public Product readProductDataForUpdate(Product existing) {
    Product.ProductBuilder builder = Product.builder(existing); // Copy existing values

    updateFieldIfProvided(builder::name, "name", existing.getName());
    updateFieldIfProvided(builder::description, "description", existing.getDescription());
    updateFieldIfProvided(builder::category, "category", existing.getCategory());
    updateFieldIfProvided(builder::brand, "brand", existing.getBrand());
    updatePriceIfProvided(builder, existing.getPrice());

    return builder.build();
  }

  private void updateFieldIfProvided(
      Consumer<String> setter, String fieldName, String currentValue) {
    String input =
        inputHandler.readString(
            String.format(
                "Enter product %s (current: %s%s",
                fieldName, currentValue, PRESS_ENTER_TO_KEEP_MESSAGE));
    if (!input.isEmpty()) {
      setter.accept(input);
    }
  }

  private void updatePriceIfProvided(Product.ProductBuilder builder, BigDecimal currentPrice) {
    String priceInput =
        inputHandler.readString(
            String.format(
                "Enter product price (current: %s%s", currentPrice, PRESS_ENTER_TO_KEEP_MESSAGE));

    if (!priceInput.isEmpty()) {
      try {
        builder.price(new BigDecimal(priceInput));
      } catch (NumberFormatException e) {
        consoleIO.printError("Invalid price format. Keeping current price.");
      }
    }
  }
}

package org.example.console.handler.impl;

import java.util.List;
import java.util.Optional;

import org.example.console.ui.ConsoleUI;
import org.example.exception.AccessDeniedException;
import org.example.model.Product;
import org.example.service.ProductService;

public class ProductHandler {
  private final ConsoleUI consoleUI;
  private final ProductService productService;

  public ProductHandler(ConsoleUI consoleUI, ProductService productService) {
    this.consoleUI = consoleUI;
    this.productService = productService;
  }

  public void handleAddProduct() {
    try {
      consoleUI.printMessage("Add New Product");
      Optional<Product> product = consoleUI.readProductData();
      if (product.isEmpty()) {
        consoleUI.printMessage("Product creation cancelled or invalid input.");
        return;
      }
      Product createdProduct = productService.addProduct(product.get());
      consoleUI.printMessage("Product added successfully!");
      consoleUI.displayProduct(createdProduct);
    } catch (AccessDeniedException | IllegalArgumentException e) {
      consoleUI.printError(e.getMessage());
    }
  }

  public void handleEditProduct() {
    try {
      Long id = consoleUI.readLong("Enter product ID to edit: ");
      Optional<Product> existing = productService.findById(id);
      if (existing.isEmpty()) {
        consoleUI.printError("Product not found.");
        return;
      }

      consoleUI.printMessage("Edit Product (leave blank to keep current value)");
      consoleUI.displayProduct(existing.orElse(null));

      Product updatedProduct = consoleUI.readProductDataForUpdate(existing.orElse(null));
      Product updated = productService.updateProduct(id, updatedProduct);
      consoleUI.printMessage("Product updated successfully!");
      consoleUI.displayProduct(updated);
    } catch (AccessDeniedException | IllegalArgumentException e) {
      consoleUI.printError(e.getMessage());
    }
  }

  public void handleDeleteProduct() {
    Long id = consoleUI.readLong("Enter product ID to delete: ");
    Optional<Product> product = productService.findById(id);

    if (product.isEmpty()) {
      consoleUI.printError("Product not found.");
      return;
    }

    consoleUI.displayProduct(product.get());
    String confirm =
        consoleUI.readString("Are you sure you want to delete this product? (yes/no): ");

    if ("yes".equalsIgnoreCase(confirm)) {
      try {
        if (productService.deleteProduct(id)) {
          consoleUI.printMessage("Product deleted successfully!");
        } else {
          consoleUI.printError("Failed to delete product.");
        }
      } catch (AccessDeniedException e) {
        consoleUI.printError(e.getMessage());
      }
    } else {
      consoleUI.printMessage("Deletion cancelled.");
    }
  }

  public void handleViewAllProducts() {
    List<Product> products = productService.getAllProducts();
    consoleUI.displayProducts(products);
  }

  public void handleClearCache() {
    try {
      productService.clearCache();
      consoleUI.printMessage("Product cache cleared successfully!");
    } catch (AccessDeniedException e) {
      consoleUI.printError(e.getMessage());
    }
  }
}

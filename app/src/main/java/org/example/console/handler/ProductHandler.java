package org.example.console.handler;

import java.util.List;
import org.example.exception.AccessDeniedException;
import org.example.model.Product;
import org.example.console.ConsoleUI;
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
            Product productData = consoleUI.readProductData();

            Product product = productService.addProduct(
                productData.getName(),
                productData.getDescription(),
                productData.getCategory(),
                productData.getBrand(),
                productData.getPrice());
            consoleUI.printMessage("Product added successfully!");
            consoleUI.displayProduct(product);
            consoleUI.pressEnterToContinue();
        } catch (AccessDeniedException e) {
            consoleUI.printError(e.getMessage());
            consoleUI.pressEnterToContinue();
        } catch (IllegalArgumentException e) {
            consoleUI.printError(e.getMessage());
            consoleUI.pressEnterToContinue();
        }
    }

    public void handleEditProduct() {
        String id = consoleUI.readString("Enter product ID to edit: ");
        Product existing = productService.findById(id);

        if (existing == null) {
            consoleUI.printError("Product not found.");
            consoleUI.pressEnterToContinue();
            return;
        }

        try {
            consoleUI.printMessage("Edit Product (leave blank to keep current value)");
            consoleUI.displayProduct(existing);

            Product updatedData = consoleUI.readProductDataForUpdate(existing);
            Product updated = productService.updateProduct(
                id,
                updatedData.getName(),
                updatedData.getDescription(),
                updatedData.getCategory(),
                updatedData.getBrand(),
                updatedData.getPrice());
            consoleUI.printMessage("Product updated successfully!");
            consoleUI.displayProduct(updated);
            consoleUI.pressEnterToContinue();
        } catch (AccessDeniedException e) {
            consoleUI.printError(e.getMessage());
            consoleUI.pressEnterToContinue();
        } catch (IllegalArgumentException e) {
            consoleUI.printError(e.getMessage());
            consoleUI.pressEnterToContinue();
        }
    }

    public void handleDeleteProduct() {
        String id = consoleUI.readString("Enter product ID to delete: ");
        Product product = productService.findById(id);

        if (product == null) {
            consoleUI.printError("Product not found.");
            consoleUI.pressEnterToContinue();
            return;
        }

        consoleUI.displayProduct(product);
        String confirm = consoleUI.readString("Are you sure you want to delete this product? (yes/no): ");

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
        consoleUI.pressEnterToContinue();
    }

    public void handleViewAllProducts() {
        List<Product> products = productService.getAllProducts();
        consoleUI.displayProducts(products);
        consoleUI.pressEnterToContinue();
    }

    public void handleClearCache() {
        try {
            productService.clearCache();
            consoleUI.printMessage("Product cache cleared successfully!");
        } catch (AccessDeniedException e) {
            consoleUI.printError(e.getMessage());
        }
        consoleUI.pressEnterToContinue();
    }
}


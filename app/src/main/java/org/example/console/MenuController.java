package org.example.console;

import java.math.BigDecimal;
import java.util.List;
import org.example.model.Product;
import org.example.repository.AuditRepository;
import org.example.service.AuthService;
import org.example.service.ProductService;

public class MenuController {
    private static final String INVALID_OPTION_MESSAGE = "Invalid option. Please try again.";
    private static final String SELECT_OPTION_MESSAGE = "Select an option: ";
    private static final String BACK_TO_MAIN_MENU_MESSAGE = "Back to Main Menu";
    private final ConsoleUI consoleUI;
    private final AuthService authService;
    private final ProductService productService;
    private final AuditRepository auditRepository;
    private boolean running;

    public MenuController(ConsoleUI consoleUI, AuthService authService, ProductService productService, AuditRepository auditRepository) {
        this.consoleUI = consoleUI;
        this.authService = authService;
        this.productService = productService;
        this.auditRepository = auditRepository;
        this.running = true;
    }

    public void start() {
        while (running) {
            if (!authService.isAuthenticated()) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showLoginMenu() {
        String[] options = {"Login", "Exit"};
        consoleUI.printMenu("Product Catalog - Login", options);
        int choice = consoleUI.readInt(SELECT_OPTION_MESSAGE);

        switch (choice) {
            case 1:
                handleLogin();
                break;
            case 2:
                running = false;
                consoleUI.printMessage("Goodbye!");
                break;
            default:
                consoleUI.printError(INVALID_OPTION_MESSAGE);
        }
    }

    private void showMainMenu() {
        String[] options = {"Product Management", "Search & Filter", "View Audit Log", "Logout"};
        consoleUI.printMenu("Product Catalog - Main Menu", options);
        int choice = consoleUI.readInt(SELECT_OPTION_MESSAGE);

        switch (choice) {
            case 1:
                showProductManagementMenu();
                break;
            case 2:
                showSearchFilterMenu();
                break;
            case 3:
                showAuditLogMenu();
                break;
            case 4:
                handleLogout();
                break;
            default:
                consoleUI.printError(INVALID_OPTION_MESSAGE);
        }
    }

    private void handleLogin() {
        String username = consoleUI.readString("Enter username: ");
        String password = consoleUI.readString("Enter password: ");

        if (authService.login(username, password)) {
            consoleUI.printMessage("Login successful! Welcome, " + username + ".");
            consoleUI.pressEnterToContinue();
        } else {
            consoleUI.printError("Login failed. Invalid username or password.");
            consoleUI.pressEnterToContinue();
        }
    }

    private void showProductManagementMenu() {
        String[] options = {"Add Product", "Edit Product", "Delete Product", "View All Products", "Clear Cache", BACK_TO_MAIN_MENU_MESSAGE};
        consoleUI.printMenu("Product Management", options);
        int choice = consoleUI.readInt(SELECT_OPTION_MESSAGE);

        switch (choice) {
            case 1:
                handleAddProduct();
                break;
            case 2:
                handleEditProduct();
                break;
            case 3:
                handleDeleteProduct();
                break;
            case 4:
                handleViewAllProducts();
                break;
            case 5:
                handleClearCache();
                break;
            case 6:
                // Back to main menu
                break;
            default:
                consoleUI.printError(INVALID_OPTION_MESSAGE);
        }
    }

    private void showSearchFilterMenu() {
        String[] options = {"Search by Name", "Filter by Category", "Filter by Brand", "Filter by Price Range", "Combined Filters", BACK_TO_MAIN_MENU_MESSAGE};
        consoleUI.printMenu("Search & Filter", options);
        int choice = consoleUI.readInt(SELECT_OPTION_MESSAGE);

        switch (choice) {
            case 1:
                handleSearchByName();
                break;
            case 2:
                handleFilterByCategory();
                break;
            case 3:
                handleFilterByBrand();
                break;
            case 4:
                handleFilterByPriceRange();
                break;
            case 5:
                handleCombinedFilters();
                break;
            case 6:
                // Back to main menu
                break;
            default:
                consoleUI.printError(INVALID_OPTION_MESSAGE);
        }
    }

    private void showAuditLogMenu() {
        String[] options = {"View All Audit Logs", "View My Audit Logs", BACK_TO_MAIN_MENU_MESSAGE};
        consoleUI.printMenu("Audit Log", options);
        int choice = consoleUI.readInt(SELECT_OPTION_MESSAGE);

        switch (choice) {
            case 1:
                List<org.example.model.AuditLog> allLogs = auditRepository.findAll();
                consoleUI.displayAuditLogs(allLogs);
                consoleUI.pressEnterToContinue();
                break;
            case 2:
                String username = authService.getCurrentUser().getUsername();
                List<org.example.model.AuditLog> userLogs = auditRepository.findByUsername(username);
                consoleUI.displayAuditLogs(userLogs);
                consoleUI.pressEnterToContinue();
                break;
            case 3:
                // Back to main menu
                break;
            default:
                consoleUI.printError(INVALID_OPTION_MESSAGE);
        }
    }

    private void handleLogout() {
        authService.logout();
        consoleUI.printMessage("Logged out successfully.");
        consoleUI.pressEnterToContinue();
    }

    private void handleAddProduct() {
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
        } catch (IllegalArgumentException e) {
            consoleUI.printError(e.getMessage());
            consoleUI.pressEnterToContinue();
        }
    }

    private void handleEditProduct() {
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
        } catch (IllegalArgumentException e) {
            consoleUI.printError(e.getMessage());
            consoleUI.pressEnterToContinue();
        }
    }

    private void handleDeleteProduct() {
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
            if (productService.deleteProduct(id)) {
                consoleUI.printMessage("Product deleted successfully!");
            } else {
                consoleUI.printError("Failed to delete product.");
            }
        } else {
            consoleUI.printMessage("Deletion cancelled.");
        }
        consoleUI.pressEnterToContinue();
    }

    private void handleViewAllProducts() {
        List<Product> products = productService.getAllProducts();
        consoleUI.displayProducts(products);
        consoleUI.pressEnterToContinue();
    }

    private void handleClearCache() {
        productService.clearCache();
        consoleUI.printMessage("Product cache cleared successfully!");
        consoleUI.pressEnterToContinue();
    }

    private void handleSearchByName() {
        String name = consoleUI.readString("Enter product name to search: ");
        List<Product> results = productService.searchByName(name);
        consoleUI.displayProducts(results);
        consoleUI.pressEnterToContinue();
    }

    private void handleFilterByCategory() {
        String category = consoleUI.readString("Enter category to filter: ");
        List<Product> results = productService.filterByCategory(category);
        consoleUI.displayProducts(results);
        consoleUI.pressEnterToContinue();
    }

    private void handleFilterByBrand() {
        String brand = consoleUI.readString("Enter brand to filter: ");
        List<Product> results = productService.filterByBrand(brand);
        consoleUI.displayProducts(results);
        consoleUI.pressEnterToContinue();
    }

    private void handleFilterByPriceRange() {
        String minInput = consoleUI.readString("Enter minimum price (press Enter to skip): ");
        BigDecimal minPrice = minInput.isEmpty() ? null : new BigDecimal(minInput);

        String maxInput = consoleUI.readString("Enter maximum price (press Enter to skip): ");
        BigDecimal maxPrice = maxInput.isEmpty() ? null : new BigDecimal(maxInput);

        List<Product> results = productService.filterByPriceRange(minPrice, maxPrice);
        consoleUI.displayProducts(results);
        consoleUI.pressEnterToContinue();
    }

    private void handleCombinedFilters() {
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

        List<Product> results = productService.applyCombinedFilters(category, brand, minPrice, maxPrice);
        consoleUI.displayProducts(results);
        consoleUI.pressEnterToContinue();
    }
}


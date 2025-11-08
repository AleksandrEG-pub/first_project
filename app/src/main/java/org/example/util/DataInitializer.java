package org.example.util;

import java.math.BigDecimal;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.AuthService;
import org.example.service.ProductService;

public class DataInitializer {
  private DataInitializer() {
  }

  public static void initializeDefaultData(
      UserRepository userRepository, ProductService productService) {
    // Create default admin user (username: admin, password: admin)
    String adminPasswordHash = AuthService.hashPassword("admin");
    User adminUser = new User("admin", adminPasswordHash, Role.ADMIN);
    userRepository.save(adminUser);

    // Create default regular user (username: user, password: user)
    String userPasswordHash = AuthService.hashPassword("user");
    User regularUser = new User("user", userPasswordHash, Role.USER);
    userRepository.save(regularUser);

    // Add sample products
    try {
      productService.addProduct(
          "Laptop Pro 15",
          "High-performance laptop with 16GB RAM and 512GB SSD",
          "Electronics",
          "TechBrand",
          new BigDecimal("1299.99"));
      productService.addProduct(
          "Wireless Mouse",
          "Ergonomic wireless mouse with long battery life",
          "Electronics",
          "TechBrand",
          new BigDecimal("29.99"));
      productService.addProduct(
          "Office Chair",
          "Comfortable ergonomic office chair with lumbar support",
          "Furniture",
          "ComfortSeat",
          new BigDecimal("199.99"));
      productService.addProduct(
          "Desk Lamp",
          "LED desk lamp with adjustable brightness",
          "Furniture",
          "BrightLight",
          new BigDecimal("49.99"));
      productService.addProduct(
          "Running Shoes",
          "Lightweight running shoes with cushioned sole",
          "Sports",
          "SportMax",
          new BigDecimal("89.99"));
      productService.addProduct(
          "Yoga Mat",
          "Non-slip yoga mat with carrying strap",
          "Sports",
          "FitLife",
          new BigDecimal("24.99"));
      productService.addProduct(
          "Coffee Maker",
          "Programmable coffee maker with thermal carafe",
          "Appliances",
          "BrewMaster",
          new BigDecimal("79.99"));
      productService.addProduct(
          "Blender",
          "High-speed blender for smoothies and soups",
          "Appliances",
          "BrewMaster",
          new BigDecimal("129.99"));
    } catch (Exception e) {
      // Ignore errors during initialization - products may already exist
      System.err.println("Warning: Could not initialize all sample products: " + e.getMessage());
    }
  }
}


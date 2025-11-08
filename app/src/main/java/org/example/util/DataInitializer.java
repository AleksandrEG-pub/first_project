package org.example.util;

import java.math.BigDecimal;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.AuthService;
import org.example.service.ProductService;

public class DataInitializer {
  private DataInitializer() {}

  public static void initializeDefaultData(
      UserRepository userRepository, ProductService productService) {
    createUser("Admin123!", "admin", Role.ADMIN, userRepository);
    createUser("User123!", "user", Role.USER, userRepository);

    // Add sample products (using internal method that bypasses auth for initialization)
    try {
      productService.addProductInternal(
          "Laptop Pro 15",
          "High-performance laptop with 16GB RAM and 512GB SSD",
          "Electronics",
          "TechBrand",
          new BigDecimal("1299.99"));
      productService.addProductInternal(
          "Wireless Mouse",
          "Ergonomic wireless mouse with long battery life",
          "Electronics",
          "TechBrand",
          new BigDecimal("29.99"));
      productService.addProductInternal(
          "Office Chair",
          "Comfortable ergonomic office chair with lumbar support",
          "Furniture",
          "ComfortSeat",
          new BigDecimal("199.99"));
      productService.addProductInternal(
          "Desk Lamp",
          "LED desk lamp with adjustable brightness",
          "Furniture",
          "BrightLight",
          new BigDecimal("49.99"));
      productService.addProductInternal(
          "Running Shoes",
          "Lightweight running shoes with cushioned sole",
          "Sports",
          "SportMax",
          new BigDecimal("89.99"));
      productService.addProductInternal(
          "Yoga Mat",
          "Non-slip yoga mat with carrying strap",
          "Sports",
          "FitLife",
          new BigDecimal("24.99"));
      productService.addProductInternal(
          "Coffee Maker",
          "Programmable coffee maker with thermal carafe",
          "Appliances",
          "BrewMaster",
          new BigDecimal("79.99"));
      productService.addProductInternal(
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

  private static void createUser(
      String password, String admin, Role admin1, UserRepository userRepository) {
    String adminPasswordHash = AuthService.hashPassword(password);
    User adminUser = new User(admin, adminPasswordHash, admin1);
    userRepository.save(adminUser);
  }
}

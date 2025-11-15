package org.example.util;

import java.math.BigDecimal;
import org.example.model.Product;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.ProductService;

public class DataInitializer {
  private DataInitializer() {}

  public static void initializeDefaultData(
      UserRepository userRepository, ProductService productService) {
    createUser("Admin123!", "admin", Role.ADMIN, userRepository);
    createUser("User123!", "user", Role.USER, userRepository);

    try {
      productService.addProductInternal(
          Product.builder()
              .name("Laptop Pro 15")
              .description("High-performance laptop with 16GB RAM and 512GB SSD")
              .category("Electronics")
              .brand("TechBrand")
              .price(new BigDecimal("1299.99"))
              .build());
      productService.addProductInternal(
          Product.builder()
              .name("Wireless Mouse")
              .description("Ergonomic wireless mouse with long battery life")
              .category("Electronics")
              .brand("TechBrand")
              .price(new BigDecimal("29.99"))
              .build());
      productService.addProductInternal(
          Product.builder()
              .name("Office Chair")
              .description("Comfortable ergonomic office chair with lumbar support")
              .category("Furniture")
              .brand("ComfortSeat")
              .price(new BigDecimal("199.99"))
              .build());
      productService.addProductInternal(
          Product.builder()
              .name("Desk Lamp")
              .description("LED desk lamp with adjustable brightness")
              .category("Furniture")
              .brand("BrightLight")
              .price(new BigDecimal("49.99"))
              .build());
      productService.addProductInternal(
          Product.builder()
              .name("Running Shoes")
              .description("Lightweight running shoes with cushioned sole")
              .category("Sports")
              .brand("SportMax")
              .price(new BigDecimal("89.99"))
              .build());
      productService.addProductInternal(
          Product.builder()
              .name("Yoga Mat")
              .description("Non-slip yoga mat with carrying strap")
              .category("Sports")
              .brand("FitLife")
              .price(new BigDecimal("24.99"))
              .build());
      productService.addProductInternal(
          Product.builder()
              .name("Coffee Maker")
              .description("Programmable coffee maker with thermal carafe")
              .category("Appliances")
              .brand("BrewMaster")
              .price(new BigDecimal("79.99"))
              .build());
      productService.addProductInternal(
          Product.builder()
              .name("Blender")
              .description("High-speed blender for smoothies and soups")
              .category("Appliances")
              .brand("BrewMaster")
              .price(new BigDecimal("129.99"))
              .build());
    } catch (Exception e) {
      System.err.println("Warning: Could not initialize all sample products: " + e.getMessage());
    }
  }

  private static void createUser(
      String password, String username, Role role, UserRepository userRepository) {
    String passwordHash = Passwords.hashPassword(password);
    User user = new User(username, passwordHash, role);
    userRepository.save(user);
  }
}

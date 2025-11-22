package org.example.service.impl;

import java.math.BigDecimal;

import org.example.dto.LoginResult;
import org.example.model.Product;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.AuthService;
import org.example.service.DataInitializer;
import org.example.service.ProductService;
import org.example.util.PasswordsImpl;

public class DataInitializerImpl implements DataInitializer {
  private final UserRepository userRepository;
  private final ProductService productService;
  private final AuthService authService;

  public DataInitializerImpl(
      UserRepository userRepository, ProductService productService, AuthService authService) {
    this.userRepository = userRepository;
    this.productService = productService;
    this.authService = authService;
  }

  @Override
  public void initializeDefaultData() {
    String adminPass = "Admin123!";
    String adminName = "admin";
    createUser(adminPass, adminName, Role.ADMIN, userRepository);
    createUser("User123!", "user", Role.USER, userRepository);

    try {
      LoginResult loginResult = authService.login(adminName, adminPass);
      if (loginResult.isSuccess()) {
        addProducts(productService);
        authService.logout();
      }
    } catch (Exception e) {
      System.err.println("Warning: Could not initialize all sample products: " + e.getMessage());
    }
  }

  private void createUser(
      String password, String username, Role role, UserRepository userRepository) {
    String passwordHash = new PasswordsImpl().hashPassword(password);
    User user = new User(username, passwordHash, role);
    userRepository.save(user);
  }

  private void addProducts(ProductService productService) {
    productService.addProduct(
        Product.builder()
            .name("Laptop Pro 15")
            .description("High-performance laptop with 16GB RAM and 512GB SSD")
            .category("Electronics")
            .brand("TechBrand")
            .price(new BigDecimal("1299.99"))
            .build());
    productService.addProduct(
        Product.builder()
            .name("Wireless Mouse")
            .description("Ergonomic wireless mouse with long battery life")
            .category("Electronics")
            .brand("TechBrand")
            .price(new BigDecimal("29.99"))
            .build());
    productService.addProduct(
        Product.builder()
            .name("Office Chair")
            .description("Comfortable ergonomic office chair with lumbar support")
            .category("Furniture")
            .brand("ComfortSeat")
            .price(new BigDecimal("199.99"))
            .build());
    productService.addProduct(
        Product.builder()
            .name("Desk Lamp")
            .description("LED desk lamp with adjustable brightness")
            .category("Furniture")
            .brand("BrightLight")
            .price(new BigDecimal("49.99"))
            .build());
    productService.addProduct(
        Product.builder()
            .name("Running Shoes")
            .description("Lightweight running shoes with cushioned sole")
            .category("Sports")
            .brand("SportMax")
            .price(new BigDecimal("89.99"))
            .build());
    productService.addProduct(
        Product.builder()
            .name("Yoga Mat")
            .description("Non-slip yoga mat with carrying strap")
            .category("Sports")
            .brand("FitLife")
            .price(new BigDecimal("24.99"))
            .build());
    productService.addProduct(
        Product.builder()
            .name("Coffee Maker")
            .description("Programmable coffee maker with thermal carafe")
            .category("Appliances")
            .brand("BrewMaster")
            .price(new BigDecimal("79.99"))
            .build());
    productService.addProduct(
        Product.builder()
            .name("Blender")
            .description("High-speed blender for smoothies and soups")
            .category("Appliances")
            .brand("BrewMaster")
            .price(new BigDecimal("129.99"))
            .build());
  }
}

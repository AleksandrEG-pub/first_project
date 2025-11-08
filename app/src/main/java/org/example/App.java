package org.example;

import org.example.cache.ProductCache;
import org.example.console.ConsoleUI;
import org.example.console.MenuController;
import org.example.repository.AuditRepository;
import org.example.repository.InMemoryAuditRepository;
import org.example.repository.InMemoryProductRepository;
import org.example.repository.InMemoryUserRepository;
import org.example.repository.ProductRepository;
import org.example.repository.UserRepository;
import org.example.service.AuditService;
import org.example.service.AuthService;
import org.example.service.ProductService;
import org.example.util.DataInitializer;

public class App {
  public static void main(String[] args) {
    // Initialize repositories
    ProductRepository productRepository = new InMemoryProductRepository();
    UserRepository userRepository = new InMemoryUserRepository();
    AuditRepository auditRepository = new InMemoryAuditRepository();

    // Initialize cache
    ProductCache productCache = new ProductCache(100);

    // Initialize services
    AuditService auditService = new AuditService(auditRepository);
    AuthService authService = new AuthService(userRepository, auditService);
    ProductService productService =
        new ProductService(productRepository, productCache, auditService, authService);

    // Initialize console UI and controller
    ConsoleUI consoleUI = new ConsoleUI();
    MenuController menuController =
        new MenuController(consoleUI, authService, productService, auditRepository);

    // Initialize default data
    DataInitializer.initializeDefaultData(userRepository, productService);

    // Start the application
    menuController.start();

    // Cleanup
    consoleUI.close();
  }
}

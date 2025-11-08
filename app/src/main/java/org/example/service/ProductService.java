package org.example.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.example.cache.ProductCache;
import org.example.model.AuditAction;
import org.example.model.Product;
import org.example.repository.ProductRepository;

public class ProductService {
  private static final String UNKNOWN_USER = "unknown";

  private final ProductRepository productRepository;
  private final ProductCache productCache;
  private final AuditService auditService;
  private final AuthService authService;

  public ProductService(
      ProductRepository productRepository,
      ProductCache productCache,
      AuditService auditService,
      AuthService authService) {
    this.productRepository = productRepository;
    this.productCache = productCache;
    this.auditService = auditService;
    this.authService = authService;
  }

  public Product addProduct(
      String name, String description, String category, String brand, BigDecimal price) {
    // Require ADMIN role for product modifications
    authService.requireAdmin();
    return addProductInternal(name, description, category, brand, price);
  }

  /** Internal method to add product without authentication check. Used for initialization only. */
  public Product addProductInternal(
      String name, String description, String category, String brand, BigDecimal price) {
    validateProductData(name, description, category, brand, price);

    String id = UUID.randomUUID().toString();
    Product product =
        Product.builder()
            .id(id)
            .name(name)
            .description(description)
            .category(category)
            .brand(brand)
            .price(price)
            .build();
    Product saved = productRepository.save(product);
    productCache.put(saved.getId(), saved);

    auditService.logAction(
        UNKNOWN_USER,
        AuditAction.ADD_PRODUCT,
        "Initialized product: " + saved.getId() + " - " + saved.getName());

    return saved;
  }

  private void validateProductData(
      String name, String description, String category, String brand, BigDecimal price) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Product name cannot be null or empty");
    }
    if (description == null || description.trim().isEmpty()) {
      throw new IllegalArgumentException("Product description cannot be null or empty");
    }
    if (category == null || category.trim().isEmpty()) {
      throw new IllegalArgumentException("Product category cannot be null or empty");
    }
    if (brand == null || brand.trim().isEmpty()) {
      throw new IllegalArgumentException("Product brand cannot be null or empty");
    }
    if (price == null) {
      throw new IllegalArgumentException("Product price cannot be null");
    }
    if (price.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Product price must be greater than zero");
    }
  }

  public Product updateProduct(
      String id, String name, String description, String category, String brand, BigDecimal price) {
    // Require ADMIN role for product modifications
    authService.requireAdmin();
    if (id == null || id.trim().isEmpty()) {
      throw new IllegalArgumentException("Product ID cannot be null or empty");
    }

    Product existing = findById(id);
    if (existing == null) {
      throw new IllegalArgumentException("Product not found with ID: " + id);
    }

    validateProductData(name, description, category, brand, price);

    existing.setName(name);
    existing.setDescription(description);
    existing.setCategory(category);
    existing.setBrand(brand);
    existing.setPrice(price);

    Product updated = productRepository.save(existing);
    productCache.put(updated.getId(), updated);

    String username =
        authService.getCurrentUser() != null
            ? authService.getCurrentUser().getUsername()
            : UNKNOWN_USER;
    auditService.logAction(
        username,
        AuditAction.EDIT_PRODUCT,
        "Edited product: " + updated.getId() + " - " + updated.getName());

    return updated;
  }

  public Product findById(String id) {
    if (id == null || id.trim().isEmpty()) {
      return null;
    }

    // Check cache first
    Product cached = productCache.get(id);
    if (cached != null) {
      String username =
          authService.getCurrentUser() != null
              ? authService.getCurrentUser().getUsername()
              : UNKNOWN_USER;
      auditService.logAction(username, AuditAction.VIEW_PRODUCT, "Viewed product (cached): " + id);
      return cached;
    }

    // If not in cache, get from repository and cache it
    Product product = productRepository.findById(id);
    if (product != null) {
      productCache.put(id, product);
      String username =
          authService.getCurrentUser() != null
              ? authService.getCurrentUser().getUsername()
              : UNKNOWN_USER;
      auditService.logAction(username, AuditAction.VIEW_PRODUCT, "Viewed product: " + id);
    }

    return product;
  }

  public boolean deleteProduct(String id) {
    // Require ADMIN role for product deletions
    authService.requireAdmin();
    if (id == null || id.trim().isEmpty()) {
      return false;
    }

    Product product = productRepository.findById(id);
    if (product == null) {
      return false;
    }

    boolean deleted = productRepository.delete(id);
    if (deleted) {
      productCache.remove(id);

      String username =
          authService.getCurrentUser() != null
              ? authService.getCurrentUser().getUsername()
              : UNKNOWN_USER;
      auditService.logAction(
          username,
          AuditAction.DELETE_PRODUCT,
          "Deleted product: " + id + " - " + product.getName());
    }

    return deleted;
  }

  public void clearCache() {
    // Require ADMIN role for cache operations
    authService.requireAdmin();
    productCache.clear();
  }

  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  public List<Product> searchByName(String name) {
    if (name == null || name.trim().isEmpty()) {
      return List.of();
    }

    List<Product> results = productRepository.searchByName(name);
    String username =
        authService.getCurrentUser() != null
            ? authService.getCurrentUser().getUsername()
            : UNKNOWN_USER;
    auditService.logAction(
        username,
        AuditAction.SEARCH,
        "Searched by name: '" + name + resultCountMessage(results.size()));

    return results;
  }

  private static String resultCountMessage(int size) {
    return "' - Found " + size + " results";
  }

  public List<Product> filterByCategory(String category) {
    if (category == null || category.trim().isEmpty()) {
      return List.of();
    }

    List<Product> results = productRepository.filterByCategory(category);
    String username =
        authService.getCurrentUser() != null
            ? authService.getCurrentUser().getUsername()
            : UNKNOWN_USER;
    auditService.logAction(
        username,
        AuditAction.SEARCH,
        "Filtered by category: '" + category + resultCountMessage(results.size()));

    return results;
  }

  public List<Product> filterByBrand(String brand) {
    if (brand == null || brand.trim().isEmpty()) {
      return List.of();
    }

    List<Product> results = productRepository.filterByBrand(brand);
    String username =
        authService.getCurrentUser() != null
            ? authService.getCurrentUser().getUsername()
            : UNKNOWN_USER;
    auditService.logAction(
        username,
        AuditAction.SEARCH,
        "Filtered by brand: '" + brand + resultCountMessage(results.size()));

    return results;
  }

  public List<Product> filterByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
    List<Product> results = productRepository.filterByPriceRange(minPrice, maxPrice);
    String username =
        authService.getCurrentUser() != null
            ? authService.getCurrentUser().getUsername()
            : UNKNOWN_USER;
    String range =
        "min="
            + (minPrice != null ? minPrice : "none")
            + ", max="
            + (maxPrice != null ? maxPrice : "none");
    auditService.logAction(
        username,
        AuditAction.SEARCH,
        "Filtered by price range: " + range + " - Found " + results.size() + " results");

    return results;
  }

  public List<Product> applyCombinedFilters(
      String category, String brand, BigDecimal minPrice, BigDecimal maxPrice) {
    List<Product> allProducts = productRepository.findAll();

    return allProducts.stream()
        .filter(product -> matchesCategory(product, category))
        .filter(product -> matchesBrand(product, brand))
        .filter(product -> matchesMinPrice(product, minPrice))
        .filter(product -> matchesMaxPrice(product, maxPrice))
        .toList();
  }

  private boolean matchesCategory(Product product, String category) {
    if (category == null || category.trim().isEmpty()) {
      return true;
    }
    return product.getCategory() != null && product.getCategory().equals(category.trim());
  }

  private boolean matchesBrand(Product product, String brand) {
    if (brand == null || brand.trim().isEmpty()) {
      return true;
    }
    return product.getBrand() != null && product.getBrand().equals(brand.trim());
  }

  private boolean matchesMinPrice(Product product, BigDecimal minPrice) {
    if (minPrice == null) {
      return true;
    }
    return product.getPrice() != null && product.getPrice().compareTo(minPrice) >= 0;
  }

  private boolean matchesMaxPrice(Product product, BigDecimal maxPrice) {
    if (maxPrice == null) {
      return true;
    }
    return product.getPrice() != null && product.getPrice().compareTo(maxPrice) <= 0;
  }
}

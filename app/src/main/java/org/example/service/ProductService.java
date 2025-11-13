package org.example.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.cache.ProductCache;
import org.example.model.AuditAction;
import org.example.model.Product;
import org.example.repository.ProductRepository;

public class ProductService {
  private final ProductRepository productRepository;
  private final ProductCache productCache;
  private final AuditService auditService;
  private final AuthService authService;
  private final ProductValidator productValidator;
  private final ProductSearchService productSearchService;

  public ProductService(
      ProductRepository productRepository,
      ProductCache productCache,
      AuditService auditService,
      AuthService authService,
      ProductValidator productValidator,
      ProductSearchService productSearchService) {
    this.productRepository = productRepository;
    this.productCache = productCache;
    this.auditService = auditService;
    this.authService = authService;
    this.productValidator = productValidator;
    this.productSearchService = productSearchService;
  }

  public List<Product> search(SearchCriteria criteria) {
    return productSearchService.search(criteria);
  }

  public List<Product> getAllProducts() {
    return productSearchService.getAllProducts();
  }

  public Product addProduct(Product product) {
    authService.requireAdmin();
    return addProductInternal(product);
  }

  /** Internal method to add product without authentication check. Used for initialization only. */
  public Product addProductInternal(Product product) {
    productValidator.validateProductData(product);
    String id = UUID.randomUUID().toString();
    Product newProduct = Product.builder(product).id(id).build();
    Product saved = productRepository.save(newProduct);
    productCache.put(saved.getId(), saved);
    String adminUserName = authService.getAdminUserName();
    auditService.logAction(
        adminUserName,
        AuditAction.ADD_PRODUCT,
        "Initialized product: " + saved.getId() + " - " + saved.getName());

    return saved;
  }

  public boolean deleteProduct(String id) {
    // Require ADMIN role for product deletions
    authService.requireAdmin();
    if (id == null || id.trim().isEmpty()) {
      return false;
    }
    Optional<Product> product = productRepository.findById(id);
    if (product.isEmpty()) {
      return false;
    }

    boolean deleted = productRepository.delete(id);
    if (deleted) {
      productCache.remove(id);

      String username = authService.getCurrentUser();
      auditService.logAction(
          username,
          AuditAction.DELETE_PRODUCT,
              "Deleted product: " + id + " - " + product.get().getName());
    }

    return deleted;
  }

  public Product updateProduct(String id, Product newProductData) {
    authService.requireAdmin();
    if (id == null || id.trim().isEmpty()) {
      throw new IllegalArgumentException("Product ID cannot be null or empty");
    }
    productValidator.validateProductData(newProductData);

    Optional<Product> existingOpt = findById(id);
    if (existingOpt.isEmpty()) {
      throw new IllegalArgumentException("Product not found with ID: " + id);
    }
    Product forUpdate = Product.builder(existingOpt.get())
            .name(newProductData.getName())
            .description(newProductData.getDescription())
            .category(newProductData.getCategory())
            .brand(newProductData.getBrand())
            .price(newProductData.getPrice())
            .build();

    Product updated = productRepository.save(forUpdate);
    productCache.put(updated.getId(), updated);

    String username = authService.getCurrentUser();
    auditService.logAction(
        username,
        AuditAction.EDIT_PRODUCT,
        "Edited product: " + updated.getId() + " - " + updated.getName());

    return updated;
  }

  public Optional<Product> findById(String id) {
    return productSearchService.findById(id);
  }

  public void clearCache() {
    // Require ADMIN role for cache operations
    authService.requireAdmin();
    productCache.clear();
  }
}

package org.example.service.impl;

import java.util.List;
import java.util.Optional;
import org.example.cache.ProductCache;
import org.example.model.AuditAction;
import org.example.model.Product;
import org.example.repository.ProductRepository;
import org.example.service.AuthService;
import org.example.service.ProductSearchService;
import org.example.service.ProductService;
import org.example.service.ProductValidator;
import org.example.service.SearchCriteria;

public class ProductServiceImpl implements ProductService {
  private final ProductRepository productRepository;
  private final ProductCache productCache;
  private final AuditServiceImpl auditService;
  private final AuthService authService;
  private final ProductValidator productValidator;
  private final ProductSearchService productSearchService;

  public ProductServiceImpl(
      ProductRepository productRepository,
      ProductCache productCache,
      AuditServiceImpl auditService,
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

  @Override
  public List<Product> search(SearchCriteria criteria) {
    return productSearchService.search(criteria);
  }

  @Override
  public List<Product> getAllProducts() {
    return productSearchService.getAllProducts();
  }

  @Override
  public Product addProduct(Product product) {
    authService.requireAdmin();
    productValidator.validateProductData(product);
    Product newProduct = Product.builder(product).build();
    Product saved = productRepository.save(newProduct);
    productCache.put(saved.getId(), saved);
    String adminUserName = authService.getAdminUserName();
    auditService.logAction(
            adminUserName,
            AuditAction.ADD_PRODUCT,
            "Initialized product: " + saved.getId() + " - " + saved.getName());

    return saved;
  }

  @Override
  public boolean deleteProduct(Long id) {
    authService.requireAdmin();
    if (id == null) {
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

  @Override
  public Product updateProduct(Long id, Product newProductData) {
    authService.requireAdmin();
    if (id == null) {
      throw new IllegalArgumentException("Product ID cannot be null");
    }
    productValidator.validateProductData(newProductData);

    Optional<Product> existingOpt = findById(id);
    if (existingOpt.isEmpty()) {
      throw new IllegalArgumentException("Product not found with ID: " + id);
    }
    Product forUpdate =
        Product.builder(existingOpt.get())
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

  @Override
  public Optional<Product> findById(Long id) {
    return productSearchService.findById(id);
  }

  @Override
  public void clearCache() {
    authService.requireAdmin();
    productCache.clear();
  }
}

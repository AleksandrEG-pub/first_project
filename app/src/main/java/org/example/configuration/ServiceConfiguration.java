package org.example.configuration;

import org.example.cache.ProductCache;
import org.example.repository.AuditRepository;
import org.example.repository.ProductRepository;
import org.example.repository.UserRepository;
import org.example.service.AuditService;
import org.example.service.AuditServiceImpl;
import org.example.service.AuthService;
import org.example.service.AuthServiceImpl;
import org.example.service.ProductSearchService;
import org.example.service.ProductService;
import org.example.service.ProductValidator;

public abstract class ServiceConfiguration {
  private static final int PRODUCT_CACHE_SIZE = 1000;
  protected final ProductService productService;
  protected final AuthService authService;
  protected final AuditServiceImpl auditService;
  protected final UserRepository userRepository;
  protected final AuditRepository auditRepository;
  protected final ProductRepository productRepository;

  protected ServiceConfiguration(
      ProductRepository productRepository,
      UserRepository userRepository,
      AuditRepository auditRepository) {

    this.productRepository = productRepository;
    this.userRepository = userRepository;
    this.auditRepository = auditRepository;

    ProductCache productCache = new ProductCache(PRODUCT_CACHE_SIZE);
    this.auditService = new AuditServiceImpl(auditRepository);
    this.authService = new AuthServiceImpl(userRepository, auditService);
    ProductValidator productValidator = new ProductValidator();
    ProductSearchService productSearchService =
        new ProductSearchService(productRepository, productCache, auditService, authService);
    this.productService =
        new ProductService(
            productRepository,
            productCache,
            auditService,
            authService,
            productValidator,
            productSearchService);
  }

  public ProductService getProductService() {
    return productService;
  }

  public AuthService getAuthService() {
    return authService;
  }

  public AuditService getAuditService() {
    return auditService;
  }

  public UserRepository getUserRepository() {
    return userRepository;
  }

  public AuditRepository getAuditRepository() {
    return auditRepository;
  }

  public ProductRepository getProductRepository() {
    return productRepository;
  }
}

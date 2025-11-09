package org.example.configuration;

import org.example.cache.ProductCache;
import org.example.repository.AuditRepository;
import org.example.repository.InMemoryAuditRepository;
import org.example.repository.InMemoryProductRepository;
import org.example.repository.InMemoryUserRepository;
import org.example.repository.ProductRepository;
import org.example.repository.UserRepository;
import org.example.service.AuditService;
import org.example.service.AuthService;
import org.example.service.ProductSearchService;
import org.example.service.ProductService;
import org.example.service.ProductValidator;

public class ServiceConfiguration {
  private final ProductService productService;
  private final AuthService authService;
  private final AuditService auditService;
  private final UserRepository userRepository;
  private final AuditRepository auditRepository;

  public ServiceConfiguration() {
    ProductRepository productRepository = new InMemoryProductRepository();
    this.userRepository = new InMemoryUserRepository();
    this.auditRepository = new InMemoryAuditRepository();
    ProductCache productCache = new ProductCache(100);

    this.auditService = new AuditService(auditRepository);
    this.authService = new AuthService(userRepository, auditService);
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
}

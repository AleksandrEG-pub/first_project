package org.example.configuration;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.cache.Cache;
import org.example.cache.ProductBaseCache;
import org.example.model.Product;
import org.example.repository.AuditRepository;
import org.example.repository.ProductRepository;
import org.example.repository.UserRepository;
import org.example.service.AuditService;
import org.example.service.AuthLoginAttemptService;
import org.example.service.AuthLoginAttemptServiceImpl;
import org.example.service.AuthService;
import org.example.service.DtoValidator;
import org.example.service.ProductSearchService;
import org.example.service.ProductService;
import org.example.service.impl.AuditServiceImpl;
import org.example.service.impl.AuthServiceImpl;
import org.example.service.impl.DtoValidatorImpl;
import org.example.service.impl.ProductSearchServiceImpl;
import org.example.service.impl.ProductServiceImpl;
import org.example.util.Passwords;
import org.example.util.PasswordsImpl;

public abstract class ServiceConfiguration {
  private static final int PRODUCT_CACHE_SIZE = 1000;
  protected final ProductService productService;
  protected final AuthService authService;
  protected final AuditService auditService;
  protected final UserRepository userRepository;
  protected final AuditRepository auditRepository;
  protected final ProductRepository productRepository;
  protected final DtoValidator dtoValidator;

  protected ServiceConfiguration(
      ProductRepository productRepository,
      UserRepository userRepository,
      AuditRepository auditRepository) {

    this.productRepository = productRepository;
    this.userRepository = userRepository;
    this.auditRepository = auditRepository;

    Cache<Long, Product> productCache = new ProductBaseCache(PRODUCT_CACHE_SIZE);
    this.auditService = new AuditServiceImpl(auditRepository);
    AuthLoginAttemptService authLoginAttemptService = new AuthLoginAttemptServiceImpl();
    Passwords passwords = new PasswordsImpl();
    this.authService =
        new AuthServiceImpl(userRepository, auditService, authLoginAttemptService, passwords);
    Validator validator;
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator();
    }
    this.dtoValidator = new DtoValidatorImpl(validator);
    ProductSearchService productSearchService =
        new ProductSearchServiceImpl(productRepository, productCache, auditService, authService);
    this.productService =
        new ProductServiceImpl(
            productRepository,
            productCache,
            auditService,
            authService,
            dtoValidator,
            productSearchService);
  }

  public ProductService getProductService() {
    return productService;
  }

  public AuthService getAuthService() {
    return authService;
  }

  public UserRepository getUserRepository() {
    return userRepository;
  }

  public AuditService getAuditService() {
    return auditService;
  }

  public DtoValidator getDtoValidator() {
    return dtoValidator;
  }
}

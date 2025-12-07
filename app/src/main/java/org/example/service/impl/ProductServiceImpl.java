package org.example.service.impl;

import jakarta.validation.ValidationException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.cache.Cache;
import org.example.dto.ProductForm;
import org.example.dto.SearchCriteria;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.ProductMapper;
import org.example.model.Product;
import org.example.repository.ProductRepository;
import org.example.service.AuthService;
import org.example.service.DtoValidator;
import org.example.service.ProductSearchService;
import org.example.service.ProductService;
import org.example_audit.dto.Auditable;
import org.example_audit.model.AuditAction;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
  private final ProductMapper productMapper;
  private final ProductRepository productRepository;
  private final Cache<Long, Product> productCache;
  private final AuthService authService;
  private final DtoValidator dtoValidator;
  private final ProductSearchService productSearchService;

  @Auditable(
      resource = "product",
      username = "@auditMessageBuilder.getUsername()",
      auditAction = AuditAction.SEARCH,
      message = "@auditMessageBuilder.buildSearchMessage(#args[0], #result)")
  @Override
  public List<Product> search(SearchCriteria criteria) {
    return productSearchService.search(criteria);
  }

  @Auditable(
      resource = "product",
      username = "@auditMessageBuilder.getUsername()",
      auditAction = AuditAction.SEARCH,
      message = "'Get all products. Found: ' + #result.size()")
  @Override
  public List<Product> getAllProducts() {
    return productSearchService.getAllProducts();
  }

  @Auditable(
      resource = "product",
      username = "@auditMessageBuilder.getUsername()",
      auditAction = AuditAction.DELETE,
      message = "'Removed product: ' + #args[0]")
  @Override
  public void deleteProduct(Long id) {
    authService.requireAdmin();
    if (id == null) {
      throw new ValidationException("id can not be null");
    }
    Optional<Product> product = productRepository.findById(id);
    if (product.isEmpty()) {
      throw new ResourceNotFoundException("product", String.valueOf(id));
    }

    boolean deleted = productRepository.delete(id);
    if (deleted) {
      productCache.remove(id);
    } else {
      throw new ResourceNotFoundException("product", String.valueOf(id));
    }
  }

  @Auditable(
      resource = "product",
      username = "@auditMessageBuilder.getUsername()",
      auditAction = AuditAction.EDIT,
      message = "'Updated product: ' + #args[0]")
  @Override
  public Product updateProduct(Long id, ProductForm newProductData) {
    authService.requireAdmin();
    if (id == null) {
      throw new ValidationException("Product ID cannot be null");
    }
    Product product = productMapper.toProduct(newProductData);
    dtoValidator.validate(product);
    Optional<Product> existingOpt = findById(id);
    if (existingOpt.isEmpty()) {
      throw new ResourceNotFoundException("product", String.valueOf(id));
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
    return updated;
  }

  @Auditable(
      resource = "product",
      username = "@auditMessageBuilder.getUsername()",
      auditAction = AuditAction.VIEW,
      message = "'Viewed product: ' + #args[0]")
  @Override
  public Optional<Product> findById(Long id) {
    return productSearchService.findById(id);
  }

  @Auditable(
      resource = "product",
      username = "@auditMessageBuilder.getUsername()",
      auditAction = AuditAction.CUSTOM,
      message = "Cleared product cache")
  @Override
  public void clearCache() {
    authService.requireAdmin();
    productCache.clear();
  }

  @Auditable(
      resource = "product",
      username = "@auditMessageBuilder.getUsername()",
      auditAction = AuditAction.ADD,
      message = "'Created product: ' + #result.getId()")
  @Override
  public Product create(ProductForm productForm) {
    Product product = productMapper.toProduct(productForm);
    return addProduct(product);
  }

  @Auditable(
      resource = "product",
      username = "@auditMessageBuilder.getUsername()",
      auditAction = AuditAction.ADD,
      message = "'Created product: ' + #result.getId()")
  @Override
  public Product addProduct(Product product) {
    authService.requireAdmin();
    dtoValidator.validate(product);
    Product newProduct = Product.builder(product).build();
    Product saved = productRepository.save(newProduct);
    productCache.put(saved.getId(), saved);
    return saved;
  }
}

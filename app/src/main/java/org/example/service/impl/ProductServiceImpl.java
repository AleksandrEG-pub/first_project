package org.example.service.impl;

import jakarta.validation.ValidationException;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.example.aspect.AuditProduct;
import org.example.aspect.AuditType;
import org.example.cache.Cache;
import org.example.dto.ProductForm;
import org.example.dto.SearchCriteria;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.ProductMapper;
import org.example.model.AuditAction;
import org.example.model.Product;
import org.example.repository.ProductRepository;
import org.example.service.AuthService;
import org.example.service.DtoValidator;
import org.example.service.ProductSearchService;
import org.example.service.ProductService;
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

  @AuditProduct(action = AuditAction.SEARCH, type = AuditType.SEARCH)
  @Override
  public List<Product> search(SearchCriteria criteria) {
    return productSearchService.search(criteria);
  }

  @AuditProduct(action = AuditAction.SEARCH, type = AuditType.SEARCH, message = "Get all products")
  @Override
  public List<Product> getAllProducts() {
    return productSearchService.getAllProducts();
  }

  @AuditProduct(action = AuditAction.DELETE_PRODUCT, type = AuditType.ID_BASED, message = "Removed product: [%d]")
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
      ;
    } else {
      throw new ResourceNotFoundException("product", String.valueOf(id));
    }
  }

  @AuditProduct(action = AuditAction.EDIT_PRODUCT, type = AuditType.ID_BASED, message = "Updated product: [%d]")
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

  @AuditProduct(action = AuditAction.VIEW_PRODUCT, type = AuditType.VIEW)
  @Override
  public Optional<Product> findById(Long id) {
    return productSearchService.findById(id);
  }

  @AuditProduct(action = AuditAction.CACHE_CLEAN_PRODUCT, message = "Cleared product cache")
  @Override
  public void clearCache() {
    authService.requireAdmin();
    productCache.clear();
  }

  @AuditProduct(action = AuditAction.ADD_PRODUCT, type = AuditType.ID_BASED, message = "Created product: [%d]")
  @Override
  public Product create(ProductForm productForm) {
    Product product = productMapper.toProduct(productForm);
    return addProduct(product);
  }

  @AuditProduct(action = AuditAction.ADD_PRODUCT, type = AuditType.ID_BASED, message = "Added product: [%d]")
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

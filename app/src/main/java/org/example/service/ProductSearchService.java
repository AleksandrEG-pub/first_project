package org.example.service;

import java.util.List;
import java.util.Optional;

import org.example.cache.ProductCache;
import org.example.model.AuditAction;
import org.example.model.Product;
import org.example.repository.ProductRepository;

public class ProductSearchService {
  private final ProductRepository productRepository;
  private final ProductCache productCache;
  private final AuditService auditService;
  private final AuthService authService;

  public ProductSearchService(
      ProductRepository productRepository,
      ProductCache productCache,
      AuditService auditService,
      AuthService authService) {
    this.productRepository = productRepository;
    this.productCache = productCache;
    this.auditService = auditService;
    this.authService = authService;
  }

  public List<Product> search(SearchCriteria criteria) {
    if (criteria == null || criteria.isEmpty()) {
      return getAllProducts();
    }
    List<Product> results;
    if (hasSingleFilter(criteria)) {
      results = applySingleFilter(criteria);
    } else {
      results = applyCombinedFilters(criteria);
    }
    String username = getCurrentUsername();
    auditService.logAction(
        username, AuditAction.SEARCH, buildSearchAuditMessage(criteria, results.size()));

    return results;
  }

  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  private boolean hasSingleFilter(SearchCriteria criteria) {
    int filterCount = 0;
    if (criteria.getName() != null) {
      filterCount++;
    }
    if (criteria.getCategory() != null) {
      filterCount++;
    }
    if (criteria.getBrand() != null) {
      filterCount++;
    }
    if (criteria.getMinPrice() != null || criteria.getMaxPrice() != null) {
      filterCount++;
    }

    return filterCount == 1;
  }

  private List<Product> applySingleFilter(SearchCriteria criteria) {
    if (criteria.getName() != null) {
      return productRepository.searchByName(criteria.getName());
    } else if (criteria.getCategory() != null) {
      return productRepository.filterByCategory(criteria.getCategory());
    } else if (criteria.getBrand() != null) {
      return productRepository.filterByBrand(criteria.getBrand());
    } else if (criteria.getMinPrice() != null || criteria.getMaxPrice() != null) {
      return productRepository.filterByPriceRange(criteria.getMinPrice(), criteria.getMaxPrice());
    }
    return List.of();
  }

  private List<Product> applyCombinedFilters(SearchCriteria criteria) {
    List<Product> allProducts = productRepository.findAll();
    return allProducts.stream()
        .filter(criteria::matchesName)
        .filter(criteria::matchesCategory)
        .filter(criteria::matchesBrand)
        .filter(criteria::matchesPriceRange)
        .toList();
  }

  private String getCurrentUsername() {
    return authService.getCurrentUser();
  }

  private String buildSearchAuditMessage(SearchCriteria criteria, int resultCount) {
    StringBuilder message = new StringBuilder("Search: ");

    if (criteria.getName() != null) {
      message.append("name='").append(criteria.getName()).append("' ");
    }
    if (criteria.getCategory() != null) {
      message.append("category='").append(criteria.getCategory()).append("' ");
    }
    if (criteria.getBrand() != null) {
      message.append("brand='").append(criteria.getBrand()).append("' ");
    }
    if (criteria.getMinPrice() != null || criteria.getMaxPrice() != null) {
      message
          .append("price[")
          .append(criteria.getMinPrice() != null ? criteria.getMinPrice() : "none")
          .append("-")
          .append(criteria.getMaxPrice() != null ? criteria.getMaxPrice() : "none")
          .append("] ");
    }

    message.append("- Found ").append(resultCount).append(" results");
    return message.toString();
  }


  public Optional<Product> findById(String id) {
    if (id == null || id.trim().isEmpty()) {
      return Optional.empty();
    }
    // Check cache first
    Product cached = productCache.get(id);
    if (cached != null) {
      String username = authService.getCurrentUser();
      auditService.logAction(username, AuditAction.VIEW_PRODUCT, "Viewed product (cached): " + id);
      return Optional.of(cached);
    }
    // If not in cache, get from repository and cache it
    Optional<Product> productOpt = productRepository.findById(id);
    if (productOpt.isPresent()) {
      productCache.put(id, productOpt.orElse(null));
      String username = authService.getCurrentUser();
      auditService.logAction(username, AuditAction.VIEW_PRODUCT, "Viewed product: " + id);
    }
    return productOpt;
  }
}

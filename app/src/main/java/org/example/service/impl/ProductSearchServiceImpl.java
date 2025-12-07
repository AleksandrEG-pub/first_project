package org.example.service.impl;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.example.cache.Cache;
import org.example.dto.SearchCriteria;
import org.example.model.Product;
import org.example.repository.ProductRepository;
import org.example.service.ProductSearchService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService {
  private final ProductRepository productRepository;
  private final Cache<Long, Product> productCache;

  @Override
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
    return results;
  }

  @Override
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
        .filter(criteria::matchesId)
        .filter(criteria::matchesName)
        .filter(criteria::matchesCategory)
        .filter(criteria::matchesBrand)
        .filter(criteria::matchesPriceRange)
        .toList();
  }

  @Override
  public Optional<Product> findById(Long id) {
    if (id == null) {
      return Optional.empty();
    }
    Optional<Product> cached = productCache.get(id);
    if (cached.isPresent()) {
      return cached;
    }
    Optional<Product> productOpt = productRepository.findById(id);
    if (productOpt.isPresent()) {
      productCache.put(id, productOpt.orElse(null));
    }
    return productOpt;
  }
}

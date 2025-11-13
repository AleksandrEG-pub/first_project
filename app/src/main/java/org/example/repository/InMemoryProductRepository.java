package org.example.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.example.model.Product;

public class InMemoryProductRepository implements ProductRepository {
  private final Map<String, Product> products;

  public InMemoryProductRepository() {
    this.products = new HashMap<>();
  }

  @Override
  public Product save(Product product) {
    if (product == null) {
      throw new IllegalArgumentException("Product cannot be null");
    }
    products.put(product.getId(), product);
    return product;
  }

  @Override
  public Optional<Product> findById(String id) {
    if (id == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(products.get(id));
  }

  @Override
  public List<Product> findAll() {
    return new ArrayList<>(products.values());
  }

  @Override
  public boolean delete(String id) {
    if (id == null) {
      return false;
    }
    return products.remove(id) != null;
  }

  @Override
  public List<Product> searchByName(String name) {
    if (name == null || name.trim().isEmpty()) {
      return new ArrayList<>();
    }
    String searchTerm = name.toLowerCase().trim();
    return products.values().stream()
        .filter(
            product ->
                product.getName() != null && product.getName().toLowerCase().contains(searchTerm))
        .toList();
  }

  @Override
  public List<Product> filterByCategory(String category) {
    if (category == null || category.trim().isEmpty()) {
      return new ArrayList<>();
    }
    String categoryFilter = category.trim();
    return products.values().stream()
        .filter(
            product ->
                product.getCategory() != null && product.getCategory().equals(categoryFilter))
        .toList();
  }

  @Override
  public List<Product> filterByBrand(String brand) {
    if (brand == null || brand.trim().isEmpty()) {
      return new ArrayList<>();
    }
    String brandFilter = brand.trim();
    return products.values().stream()
        .filter(product -> product.getBrand() != null && product.getBrand().equals(brandFilter))
        .toList();
  }

  @Override
  public List<Product> filterByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
    return products.values().stream()
        .filter(
            product -> {
              if (product.getPrice() == null) {
                return false;
              }
              boolean matchesMin = minPrice == null || product.getPrice().compareTo(minPrice) >= 0;
              boolean matchesMax = maxPrice == null || product.getPrice().compareTo(maxPrice) <= 0;
              return matchesMin && matchesMax;
            })
        .toList();
  }
}

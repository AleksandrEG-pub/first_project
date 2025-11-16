package org.example.repository;

import org.example.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository abstraction for managing Product entities.
 * Implementations provide basic CRUD operations and queries for Product domain objects.
 * Implementations are expected to persist data to the configured storage (in-memory, file or
 * database) and to be used by service layer components.
 */
public interface ProductRepository {
  Product save(Product product);

  Optional<Product> findById(Long id);

  List<Product> findAll();

  boolean delete(Long id);

  List<Product> searchByName(String name);

  List<Product> filterByCategory(String category);

  List<Product> filterByBrand(String brand);

  List<Product> filterByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
}

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

  /** Saves a product. Returns the saved product. */
  Product save(Product product);

  /** Finds a product by ID. Returns empty if not found. */
  Optional<Product> findById(Long id);

  /** Returns all products. */
  List<Product> findAll();

  /** Deletes product by ID. Returns true if deleted. */
  boolean delete(Long id);

  /** Searches products by name. */
  List<Product> searchByName(String name);

  /** Filters products by category. */
  List<Product> filterByCategory(String category);

  /** Filters products by brand. */
  List<Product> filterByBrand(String brand);

  /** Filters products by price range. */
  List<Product> filterByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
}
package org.example.repository;

import org.example.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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

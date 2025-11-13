package org.example.service;

import java.math.BigDecimal;
import org.example.model.Product;

public class ProductValidator {
  public void validateProductData(Product product) {
    if (product.getName() == null || product.getName().trim().isEmpty()) {
      throw new IllegalArgumentException("Product name cannot be null or empty");
    }
    if (product.getDescription() == null || product.getDescription().trim().isEmpty()) {
      throw new IllegalArgumentException("Product description cannot be null or empty");
    }
    if (product.getCategory() == null || product.getCategory().trim().isEmpty()) {
      throw new IllegalArgumentException("Product category cannot be null or empty");
    }
    if (product.getBrand() == null || product.getBrand().trim().isEmpty()) {
      throw new IllegalArgumentException("Product brand cannot be null or empty");
    }
    if (product.getPrice() == null) {
      throw new IllegalArgumentException("Product price cannot be null");
    }
    if (product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Product price must be greater than zero");
    }
  }
}

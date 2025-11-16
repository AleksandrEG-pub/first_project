package org.example.service.impl;

import java.math.BigDecimal;
import org.example.model.Product;
import org.example.service.ProductValidator;

public class ProductValidatorImpl implements ProductValidator {
  private static final int MAX_NAME_LENGTH = 255;
  private static final int MAX_DESCRIPTION_LENGTH = 10000;
  private static final int MAX_CATEGORY_LENGTH = 255;
  private static final int MAX_BRAND_LENGTH = 255;
  private static final BigDecimal MIN_PRICE = new BigDecimal("0.01");

  @Override
  public void validateProductData(Product product) {
    validateField(product.getName(), "name", MAX_NAME_LENGTH);
    validateField(product.getDescription(), "description", MAX_DESCRIPTION_LENGTH);
    validateField(product.getCategory(), "category", MAX_CATEGORY_LENGTH);
    validateField(product.getBrand(), "brand", MAX_BRAND_LENGTH);
    validatePrice(product.getPrice());
  }

  private void validateField(String value, String fieldName, int maxLength) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("Product " + fieldName + " cannot be null or empty");
    }
    if (value.length() >= maxLength) {
      throw new IllegalArgumentException(
          "Product " + fieldName + " cannot be longer than " + maxLength);
    }
  }

  private void validatePrice(BigDecimal price) {
    if (price == null) {
      throw new IllegalArgumentException("Product price cannot be null");
    }
    if (price.compareTo(MIN_PRICE) < 0) {
      throw new IllegalArgumentException("Product price must be greater than zero");
    }
  }
}

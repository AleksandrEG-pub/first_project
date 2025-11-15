package org.example.service;

import org.example.model.Product;

/** Validation for products according to business rules */
public interface ProductValidator {
  /** Full validation for all business rules related to product */
  void validateProductData(Product product);
}

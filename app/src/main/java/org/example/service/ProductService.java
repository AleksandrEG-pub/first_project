package org.example.service;

import java.util.List;
import java.util.Optional;
import org.example.model.Product;

/** Main interface for product related operations */
public interface ProductService {
  /** For given criteria search for products in application storage */
  List<Product> search(SearchCriteria criteria);

  /**
   * Retrieve all products from application storage. Caution: no pagination can cause performance
   * issues with large datasets
   */
  List<Product> getAllProducts();

  /** add product to application storage */
  Product addProduct(Product product);

  /** Remove product with given id from application storage if no product exist return false */
  boolean deleteProduct(Long id);

  /** For given id update product data */
  Product updateProduct(Long id, Product newProductData);

  /** For given id find product */
  Optional<Product> findById(Long id);

  /** Clear if exist product cache, only affects performance, not existence of data */
  void clearCache();
}

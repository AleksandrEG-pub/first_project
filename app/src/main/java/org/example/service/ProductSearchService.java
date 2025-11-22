package org.example.service;

import java.util.List;
import java.util.Optional;
import org.example.model.Product;

/**
 * Holds methods related to searching products in application storage. Serves as a delegate for
 * ProductService to separate search logic
 */
public interface ProductSearchService {
  /** Find products matching given search criteria */
  List<Product> search(SearchCriteria criteria);

  /** Find all products in application storage. Result can contain large number of items. */
  List<Product> getAllProducts();

  /** For given id find product if exist */
  Optional<Product> findById(Long id);
}

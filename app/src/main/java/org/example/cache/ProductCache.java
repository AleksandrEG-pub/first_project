package org.example.cache;

import org.example.model.Product;

public class ProductCache extends BaseCache<Long, Product> {

  public ProductCache(int maxSize) {
    super(maxSize);
  }

  public void put(Product product) {
    if (product != null && product.getId() != null) {
      put(product.getId(), product);
    }
  }
}

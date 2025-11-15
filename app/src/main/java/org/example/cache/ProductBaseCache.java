package org.example.cache;

import org.example.model.Product;

public class ProductBaseCache extends BaseCacheImpl<Long, Product> {

  public ProductBaseCache(int maxSize) {
    super(maxSize);
  }

  public void put(Product product) {
    if (product != null && product.getId() != null) {
      put(product.getId(), product);
    }
  }
}

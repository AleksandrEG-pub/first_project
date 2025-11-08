package org.example.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import org.example.model.Product;

public class ProductCache {
  private final Map<String, Product> cache;
  private final int maxSize;

  public ProductCache(int maxSize) {
    this.maxSize = maxSize;
    this.cache =
        new LinkedHashMap<>() {
          @Override
          protected boolean removeEldestEntry(Map.Entry<String, Product> eldest) {
            return size() > maxSize;
          }
        };
  }

  public Product get(String id) {
    if (id == null) {
      return null;
    }
    return cache.get(id);
  }

  public void put(String id, Product product) {
    if (id != null && product != null) {
      cache.put(id, product);
    }
  }

  public void remove(String id) {
    if (id != null) {
      cache.remove(id);
    }
  }

  public void clear() {
    cache.clear();
  }

  private int size() {
    return cache.size();
  }
}

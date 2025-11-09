package org.example.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BaseCache<K, V> {
  protected final Map<K, V> cache;
  protected final int maxSize;

  protected BaseCache(int maxSize) {
    this.maxSize = maxSize;
    this.cache = createCacheMap();
  }

  protected Map<K, V> createCacheMap() {
    return new LinkedHashMap<K, V>(16, 0.75f, true) {
      @Override
      protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
      }
    };
  }

  public int size() {
    return cache.size();
  }

  public V get(K key) {
    if (key == null) {
      return null;
    }

    return cache.get(key);
  }

  public void put(K key, V value) {
    if (key != null && value != null) {
      cache.put(key, value);
    }
  }

  public void remove(K key) {
    if (key != null) {
      cache.remove(key);
    }
  }

  public void clear() {
    cache.clear();
  }

  public boolean isEmpty() {
    return cache.isEmpty();
  }
}

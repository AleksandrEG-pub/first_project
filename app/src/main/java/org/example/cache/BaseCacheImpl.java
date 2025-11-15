package org.example.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public abstract class BaseCacheImpl<K, V> implements Cache<K,V> {
  protected final Map<K, V> cache;
  protected final int maxSize;

  protected BaseCacheImpl(int maxSize) {
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

  @Override
  public int size() {
    return cache.size();
  }

  @Override
  public Optional<V> get(K key) {
    if (key == null) {
      return Optional.empty();
    }

    return Optional.ofNullable(cache.get(key));
  }

  @Override
  public void put(K key, V value) {
    if (key != null && value != null) {
      cache.put(key, value);
    }
  }

  @Override
  public void remove(K key) {
    if (key != null) {
      cache.remove(key);
    }
  }

  @Override
  public void clear() {
    cache.clear();
  }

  @Override
  public boolean isEmpty() {
    return cache.isEmpty();
  }
}

package org.example.cache;

import java.util.Optional;

/** Provides a generic cache interface for storing and retrieving key-value pairs */
public interface Cache<K, V> {
  /** current size of cache */
  int size();

  /** got given key return a cache entry if exist */
  Optional<V> get(K key);

  /** associate some key with some value */
  void put(K key, V value);

  /** for given key remove entry from cache, does not throw exceptions if not exist */
  void remove(K key);

  /** remove all entries from cache */
  void clear();

  /** return true if cache has no entries */
  boolean isEmpty();
}

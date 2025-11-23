package org.example.repository;

import org.example.model.User;

/**
 * Repository abstraction for managing User entities. Provides operations to create, read, update
 * and delete user accounts and to query users by identifiers or unique attributes.
 */
public interface UserRepository {
  User findByUsername(String username);

  /** Saves a user. Returns the saved user. */
  User save(User user);
}

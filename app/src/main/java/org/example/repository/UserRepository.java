package org.example.repository;

import org.example.model.User;

public interface UserRepository {
  User findByUsername(String username);

  User save(User user);
}

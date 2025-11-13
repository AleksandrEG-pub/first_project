package org.example.repository.impl.in_memory;

import org.example.model.User;
import org.example.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryUserRepository implements UserRepository {
  private final Map<String, User> users;

  public InMemoryUserRepository() {
    this.users = new HashMap<>();
  }

  @Override
  public User findByUsername(String username) {
    if (username == null) {
      return null;
    }
    return users.get(username);
  }

  @Override
  public User save(User user) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }
    users.put(user.getUsername(), user);
    return user;
  }
}

package org.example.repository.impl.in_memory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.example.model.User;
import org.example.repository.UserRepository;

public class InMemoryUserRepository implements UserRepository {
  private static final AtomicLong counter = new AtomicLong(0);
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
    if (user.getId() == null) {
      user.setId(counter.getAndIncrement());
    }
    users.put(user.getUsername(), user);
    return user;
  }
}

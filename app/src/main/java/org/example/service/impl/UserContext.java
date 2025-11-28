package org.example.service.impl;

import java.util.Optional;
import org.example.exception.ApplicationException;
import org.example.model.User;

public class UserContext {

  private UserContext() {}

  private static final ThreadLocal<User> currentUser = new ThreadLocal<>();

  public static Optional<User> getCurrentUser() {
    return Optional.ofNullable(currentUser.get());
  }

  public static void setCurrentUser(User user) {
    currentUser.set(user);
  }

  public static User getValidatedCurrentUser() {
    User user = currentUser.get();
    if (user == null) {
      throw new ApplicationException("current user can not be null");
    }
    return user;
  }

  public static void remove() {
    currentUser.remove();
  }
}

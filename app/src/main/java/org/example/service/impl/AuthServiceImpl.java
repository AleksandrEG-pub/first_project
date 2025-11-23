package org.example.service.impl;

import org.example.dto.LoginResult;
import org.example.exception.AccessDeniedException;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.AuthLoginAttemptService;
import org.example.service.AuthService;
import org.example.util.Passwords;

public class AuthServiceImpl implements AuthService {
  private static final String ADMIN = "admin";

  private final UserRepository userRepository;
  private final AuthLoginAttemptService authLoginAttemptService;

  private final Passwords passwords;

  public AuthServiceImpl(
      UserRepository userRepository,
      AuthLoginAttemptService authLoginAttemptService,
      Passwords passwords) {
    this.userRepository = userRepository;
    this.authLoginAttemptService = authLoginAttemptService;
    this.passwords = passwords;
  }

  @Override
  public LoginResult login(String username, String password) {
    if (username == null || password == null) {
      return new LoginResult("login requires username and password");
    }
    if (authLoginAttemptService.isAccountLocked(username)) {
      return new LoginResult("Login failed: account locked due to too many attempts");
    }
    User user = userRepository.findByUsername(username);
    if (user == null) {
      authLoginAttemptService.recordFailedAttempt(username);
      return new LoginResult("Login failed: user not found");
    }
    if (!passwords.verifyPassword(password, user.getPasswordHash())) {
      authLoginAttemptService.recordFailedAttempt(username);
      return new LoginResult("Login failed: invalid password");
    }
    authLoginAttemptService.remove(username);
    UserContext.setCurrentUser(user);
    return new LoginResult(true, "Login successful");
  }

  @Override
  public void requireAdmin() {
    if (!isAdmin()) {
      throw new AccessDeniedException("Admin role required for this operation");
    }
  }

  @Override
  public boolean isAdmin() {
    User currentUser = UserContext.getValidatedCurrentUser();
    return isAuthenticated() && currentUser.getRole() == Role.ADMIN;
  }

  @Override
  public boolean isAuthenticated() {
    User currentUser = UserContext.getValidatedCurrentUser();
    return currentUser != User.anonymous();
  }

  @Override
  public void logout() {
    UserContext.setCurrentUser(User.anonymous());
  }

  @Override
  public User getCurrentUser() {
    return UserContext.getValidatedCurrentUser();
  }

  @Override
  public String getAdminUserName() {
    return ADMIN;
  }
}

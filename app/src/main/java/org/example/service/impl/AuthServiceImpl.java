package org.example.service.impl;

import org.example.dto.LoginResult;
import org.example.exception.AccessDeniedException;
import org.example.model.AuditAction;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.AuditService;
import org.example.service.AuthLoginAttemptService;
import org.example.service.AuthService;
import org.example.util.Passwords;

public class AuthServiceImpl implements AuthService {
  private static final String UNKNOWN_USER = "unknown";
  private static final String ADMIN = "admin";

  private final UserRepository userRepository;
  private final AuditService auditService;
  private final AuthLoginAttemptService authLoginAttemptService;

  private final Passwords passwords;
  private User currentUser;

  public AuthServiceImpl(
      UserRepository userRepository,
      AuditService auditService,
      AuthLoginAttemptService authLoginAttemptService,
      Passwords passwords) {
    this.userRepository = userRepository;
    this.auditService = auditService;
    this.authLoginAttemptService = authLoginAttemptService;
    this.passwords = passwords;
  }

  @Override
  public LoginResult login(String username, String password) {
    if (username == null || password == null) {
      return new LoginResult("login requires username and password");
    }

    if (authLoginAttemptService.isAccountLocked(username)) {
      String details = "Login failed: account locked due to too many attempts";
      auditService.logAction(username, AuditAction.LOGIN, details);
      return new LoginResult(details);
    }

    User user = userRepository.findByUsername(username);
    if (user == null) {
      authLoginAttemptService.recordFailedAttempt(username);
      String details = "Login failed: user not found";
      auditService.logAction(username, AuditAction.LOGIN, details);
      return new LoginResult(details);
    }

    if (!passwords.verifyPassword(password, user.getPasswordHash())) {
      authLoginAttemptService.recordFailedAttempt(username);
      String details = "Login failed: invalid password";
      auditService.logAction(username, AuditAction.LOGIN, details);
      return new LoginResult(details);
    }

    authLoginAttemptService.remove(username);
    currentUser = user;
    auditService.logAction(username, AuditAction.LOGIN, "Login successful");
    return new LoginResult(true);
  }

  @Override
  public void requireAdmin() {
    if (!isAdmin()) {
      String username = currentUser != null ? currentUser.getUsername() : "anonymous";
      auditService.logAction(username, AuditAction.LOGIN, "Access denied: admin role required");
      throw new AccessDeniedException("Admin role required for this operation");
    }
  }

  @Override
  public boolean isAdmin() {
    return isAuthenticated() && currentUser.getRole() == Role.ADMIN;
  }

  @Override
  public boolean isAuthenticated() {
    return currentUser != null;
  }

  @Override
  public void logout() {
    if (currentUser != null) {
      String username = currentUser.getUsername();
      currentUser = null;
      auditService.logAction(username, AuditAction.LOGOUT, "Logout successful");
    }
  }

  @Override
  public String getCurrentUser() {
    return currentUser != null ? currentUser.getUsername() : UNKNOWN_USER;
  }

  @Override
  public String getAdminUserName() {
    return ADMIN;
  }
}

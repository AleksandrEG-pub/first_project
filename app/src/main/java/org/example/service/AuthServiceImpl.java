package org.example.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.example.exception.AccessDeniedException;
import org.example.model.AuditAction;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.util.Passwords;

public class AuthServiceImpl implements AuthService {
  private static final String UNKNOWN_USER = "unknown";
  private static final String ADMIN = "admin";
  private static final int MAX_LOGIN_ATTEMPTS = 3;
  private static final int LOCKOUT_DURATION_MINUTES = 3;

  private final UserRepository userRepository;
  private final AuditServiceImpl auditService;
  private final Map<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();
  private User currentUser;

  public AuthServiceImpl(UserRepository userRepository, AuditServiceImpl auditService) {
    this.userRepository = userRepository;
    this.auditService = auditService;
  }

  @Override
  public boolean login(String username, String password) {
    if (username == null || password == null) {
      return false;
    }

    if (isAccountLocked(username)) {
      auditService.logAction(
          username, AuditAction.LOGIN, "Login failed: account locked due to too many attempts");
      return false;
    }

    User user = userRepository.findByUsername(username);
    if (user == null) {
      recordFailedAttempt(username);
      auditService.logAction(username, AuditAction.LOGIN, "Login failed: user not found");
      return false;
    }

    if (!Passwords.verifyPassword(password, user.getPasswordHash())) {
      recordFailedAttempt(username);
      auditService.logAction(username, AuditAction.LOGIN, "Login failed: invalid password");
      return false;
    }

    loginAttempts.remove(username);
    currentUser = user;
    auditService.logAction(username, AuditAction.LOGIN, "Login successful");
    return true;
  }

  private boolean isAccountLocked(String username) {
    LoginAttempt attempt = loginAttempts.get(username);
    if (attempt == null) {
      return false;
    }
    if (attempt.isLockoutExpired()) {
      loginAttempts.remove(username);
      return false;
    }
    return attempt.getCount() >= MAX_LOGIN_ATTEMPTS;
  }

  private void recordFailedAttempt(String username) {
    LoginAttempt attempt = loginAttempts.getOrDefault(username, new LoginAttempt());
    attempt.increment();
    loginAttempts.put(username, attempt);
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

  private static class LoginAttempt {
    private int count;
    private LocalDateTime lastAttempt;

    public LoginAttempt() {
      this.count = 0;
      this.lastAttempt = LocalDateTime.now();
    }

    public void increment() {
      this.count++;
      this.lastAttempt = LocalDateTime.now();
    }

    public int getCount() {
      return count;
    }

    public boolean isLockoutExpired() {
      if (count < MAX_LOGIN_ATTEMPTS) {
        return false;
      }
      return lastAttempt.plusMinutes(LOCKOUT_DURATION_MINUTES).isBefore(LocalDateTime.now());
    }
  }
}

package org.example.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.example.exception.PasswordHashingException;
import org.example.model.AuditAction;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.UserRepository;

public class AuthService {
  private final UserRepository userRepository;
  private final AuditService auditService;
  private User currentUser;

  public AuthService(UserRepository userRepository, AuditService auditService) {
    this.userRepository = userRepository;
    this.auditService = auditService;
  }

  public boolean login(String username, String password) {
    if (username == null || password == null) {
      return false;
    }

    User user = userRepository.findByUsername(username);
    if (user == null) {
      auditService.logAction(username, AuditAction.LOGIN, "Login failed: user not found");
      return false;
    }

    String passwordHash = hashPassword(password);
    if (!user.getPasswordHash().equals(passwordHash)) {
      auditService.logAction(username, AuditAction.LOGIN, "Login failed: invalid password");
      return false;
    }

    currentUser = user;
    auditService.logAction(username, AuditAction.LOGIN, "Login successful");
    return true;
  }

  public static String hashPassword(String password) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = md.digest(password.getBytes());
      StringBuilder sb = new StringBuilder();
      for (byte b : hashBytes) {
        sb.append(String.format("%02x", b));
      }
      return sb.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new PasswordHashingException("SHA-256 algorithm not available", e);
    }
  }

  public void logout() {
    if (currentUser != null) {
      String username = currentUser.getUsername();
      currentUser = null;
      auditService.logAction(username, AuditAction.LOGOUT, "Logout successful");
    }
  }

  public User getCurrentUser() {
    return currentUser;
  }

  public boolean isAuthenticated() {
    return currentUser != null;
  }
}

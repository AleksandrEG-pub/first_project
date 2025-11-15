package org.example.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.example.exception.AccessDeniedException;
import org.example.exception.PasswordHashingException;
import org.example.exception.PasswordValidationException;
import org.example.model.AuditAction;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.UserRepository;

public class AuthService {
  private static final String UNKNOWN_USER = "unknown";
  private static final String ADMIN = "admin";
  private static final int MAX_LOGIN_ATTEMPTS = 3;
  private static final int LOCKOUT_DURATION_MINUTES = 3;
  private static final int MIN_PASSWORD_LENGTH = 8;
  private static final int PBKDF2_ITERATIONS = 100000;
  private static final int SALT_LENGTH = 16;
  private static final int KEY_LENGTH = 256;
  private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";

  private final UserRepository userRepository;
  private final AuditServiceImpl auditService;
  // Rate limiting: track failed login attempts per username
  private final Map<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();
  private User currentUser;

  public AuthService(UserRepository userRepository, AuditServiceImpl auditService) {
    this.userRepository = userRepository;
    this.auditService = auditService;
  }

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

    if (!verifyPassword(password, user.getPasswordHash())) {
      recordFailedAttempt(username);
      auditService.logAction(username, AuditAction.LOGIN, "Login failed: invalid password");
      return false;
    }

    // Successful login - clear failed attempts
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

  private boolean verifyPassword(String password, String storedHash) {
    try {
      // Parse stored hash: format is salt:hash
      String[] parts = storedHash.split(":");
      if (parts.length != 2) {
        return false;
      }
      byte[] salt = Base64.getDecoder().decode(parts[0]);
      byte[] storedHashBytes = Base64.getDecoder().decode(parts[1]);
      // Hash the provided password with the same salt
      PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH);
      SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
      byte[] computedHash = factory.generateSecret(spec).getEncoded();

      return Arrays.equals(storedHashBytes, computedHash);
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Format: base64(salt):base64(hash)
   */
  public static String hashPassword(String password) {
    if (password == null) {
      throw new PasswordValidationException("Password cannot be null");
    }
    validatePasswordComplexity(password);
    try {
      SecureRandom random = new SecureRandom();
      byte[] salt = new byte[SALT_LENGTH];
      random.nextBytes(salt);

      PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH);
      SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
      byte[] hash = factory.generateSecret(spec).getEncoded();

      String saltBase64 = Base64.getEncoder().encodeToString(salt);
      String hashBase64 = Base64.getEncoder().encodeToString(hash);
      return saltBase64 + ":" + hashBase64;
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new PasswordHashingException("Failed to hash password", e);
    }
  }

  public static void validatePasswordComplexity(String password) {
    if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
      throw new PasswordValidationException(
          "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
    }
    boolean hasUpperCase = false;
    boolean hasLowerCase = false;
    boolean hasDigit = false;
    boolean hasSpecialChar = false;
    for (char c : password.toCharArray()) {
      if (Character.isUpperCase(c)) {
        hasUpperCase = true;
      } else if (Character.isLowerCase(c)) {
        hasLowerCase = true;
      } else if (Character.isDigit(c)) {
        hasDigit = true;
      } else if (!Character.isLetterOrDigit(c)) {
        hasSpecialChar = true;
      }
    }
    if (!hasUpperCase) {
      throw new PasswordValidationException("Password must contain at least one uppercase letter");
    }
    if (!hasLowerCase) {
      throw new PasswordValidationException("Password must contain at least one lowercase letter");
    }
    if (!hasDigit) {
      throw new PasswordValidationException("Password must contain at least one digit");
    }
    if (!hasSpecialChar) {
      throw new PasswordValidationException("Password must contain at least one special character");
    }
  }

  public void requireAdmin() {
    if (!isAdmin()) {
      String username = currentUser != null ? currentUser.getUsername() : "anonymous";
      auditService.logAction(username, AuditAction.LOGIN, "Access denied: admin role required");
      throw new AccessDeniedException("Admin role required for this operation");
    }
  }

  public boolean isAdmin() {
    return isAuthenticated() && currentUser.getRole() == Role.ADMIN;
  }

  public boolean isAuthenticated() {
    return currentUser != null;
  }

  public void logout() {
    if (currentUser != null) {
      String username = currentUser.getUsername();
      currentUser = null;
      auditService.logAction(username, AuditAction.LOGOUT, "Logout successful");
    }
  }

  public String getCurrentUser() {
    return currentUser != null ? currentUser.getUsername() : UNKNOWN_USER;
  }

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

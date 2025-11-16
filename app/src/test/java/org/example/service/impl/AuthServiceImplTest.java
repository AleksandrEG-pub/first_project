package org.example.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import org.example.exception.AccessDeniedException;
import org.example.model.AuditAction;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.AuthLoginAttemptService;
import org.example.util.Passwords;
import org.example.util.PasswordsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AuthServiceImplTest {

  private UserRepository userRepository;
  private AuditServiceImpl auditService;
  private AuthLoginAttemptService authLoginAttemptService;

  private AuthServiceImpl authService;
  private Passwords passwords;

  @BeforeEach
  void setUp() {
    userRepository = Mockito.mock(UserRepository.class);
    auditService = Mockito.mock(AuditServiceImpl.class);
    passwords = Mockito.mock(PasswordsImpl.class);
    authLoginAttemptService = Mockito.mock(AuthLoginAttemptService.class);
    authService = new AuthServiceImpl(userRepository, auditService, authLoginAttemptService, passwords);
  }

  @Test
  void login_ShouldReturnFalse_WhenUsernameIsNull() {
    // When
    boolean result = authService.login(null, "password");

    // Then
    assertThat(result).isFalse();
    verify(auditService, never()).logAction(any(), any(), any());
  }

  @Test
  void login_ShouldReturnFalse_WhenPasswordIsNull() {
    // When
    boolean result = authService.login("username", null);

    // Then
    assertThat(result).isFalse();
    verify(auditService, never()).logAction(any(), any(), any());
  }

  @Test
  void login_ShouldReturnFalse_WhenUserNotFound() {
    // Given
    String username = "nonexistent";
    String password = "password";
    when(userRepository.findByUsername(username)).thenReturn(null);

    // When
    boolean result = authService.login(username, password);

    // Then
    assertThat(result).isFalse();
    verify(userRepository).findByUsername(username);
    verify(auditService).logAction(username, AuditAction.LOGIN, "Login failed: user not found");
  }

  @Test
  void login_ShouldReturnFalse_WhenPasswordInvalid() {
    // Given
    String username = "testuser";
    String password = "wrongpassword";
    User user = createTestUser(username, Role.USER, "correct_hash");

    when(userRepository.findByUsername(username)).thenReturn(user);
    when(passwords.verifyPassword(password, user.getPasswordHash())).thenReturn(false);

    // When
    boolean result = authService.login(username, password);

    // Then
    assertThat(result).isFalse();
    verify(userRepository).findByUsername(username);
    verify(auditService).logAction(username, AuditAction.LOGIN, "Login failed: invalid password");
  }

  private User createTestUser(String username, Role role, String passwordHash) {
    User user = new User();
    user.setUsername(username);
    user.setRole(role);
    user.setPasswordHash(passwordHash);
    return user;
  }

  @Test
  void login_ShouldReturnTrue_WhenCredentialsValid() {
    // Given
    String username = "testuser";
    String password = "correctpassword";
    User user = createTestUser(username, Role.USER, "correct_hash");

    when(userRepository.findByUsername(username)).thenReturn(user);
    when(passwords.verifyPassword(password, user.getPasswordHash())).thenReturn(true);
    when(authLoginAttemptService.isAccountLocked(username)).thenReturn(false);

    // When
    boolean result = authService.login(username, password);

    // Then
    assertThat(result).isTrue();
    assertThat(authService.isAuthenticated()).isTrue();
    assertThat(authService.getCurrentUser()).isEqualTo(username);
    verify(userRepository).findByUsername(username);
    verify(auditService).logAction(username, AuditAction.LOGIN, "Login successful");
  }

  @Test
  void login_ShouldSetCurrentUser_WhenSuccessful() {
    // Given
    String username = "testuser";
    String password = "correctpassword";
    User user = createTestUser(username, Role.USER, "correct_hash");

    when(userRepository.findByUsername(username)).thenReturn(user);
    when(passwords.verifyPassword(password, user.getPasswordHash())).thenReturn(true);
    when(authLoginAttemptService.isAccountLocked(username)).thenReturn(false);

    // When
    authService.login(username, password);

    // Then
    assertThat(authService.isAuthenticated()).isTrue();
    assertThat(authService.getCurrentUser()).isEqualTo(username);
  }

  @Test
  void login_ShouldWorkForAdminUser() {
    // Given
    String username = "admin";
    String password = "adminpass";
    User adminUser = createTestUser(username, Role.ADMIN, "admin_hash");

    when(userRepository.findByUsername(username)).thenReturn(adminUser);
    when(passwords.verifyPassword(password, adminUser.getPasswordHash())).thenReturn(true);
    when(authLoginAttemptService.isAccountLocked(username)).thenReturn(false);

    // When
    boolean result = authService.login(username, password);

    // Then
    assertThat(result).isTrue();
    assertThat(authService.isAuthenticated()).isTrue();
    assertThat(authService.isAdmin()).isTrue();
    assertThat(authService.getCurrentUser()).isEqualTo(username);
  }

  @Test
  void requireAdmin_ShouldNotThrow_WhenUserIsAdmin() {
    // Given
    User adminUser = createTestUser("admin", Role.ADMIN, "hash");
    setCurrentUser(adminUser);

    // When & Then
    assertThatNoException().isThrownBy(() -> authService.requireAdmin());
  }

  // Helper method to set current user (using reflection for testing)
  private void setCurrentUser(User user) {
    try {
      Field currentUserField = AuthServiceImpl.class.getDeclaredField("currentUser");
      currentUserField.setAccessible(true);
      currentUserField.set(authService, user);
    } catch (Exception e) {
      throw new RuntimeException("Failed to set current user for testing", e);
    }
  }

  @Test
  void requireAdmin_ShouldThrowAccessDenied_WhenUserIsNotAdmin() {
    // Given
    User regularUser = createTestUser("user", Role.USER, "hash");
    setCurrentUser(regularUser);

    // When & Then
    assertThatThrownBy(() -> authService.requireAdmin())
        .isInstanceOf(AccessDeniedException.class)
        .hasMessage("Admin role required for this operation");

    verify(auditService).logAction("user", AuditAction.LOGIN, "Access denied: admin role required");
  }

  @Test
  void requireAdmin_ShouldThrowAccessDenied_WhenNotAuthenticated() {
    // Given - no current user
    authService.logout(); // Ensure no user

    // When & Then
    assertThatThrownBy(() -> authService.requireAdmin())
        .isInstanceOf(AccessDeniedException.class)
        .hasMessage("Admin role required for this operation");

    verify(auditService)
        .logAction("anonymous", AuditAction.LOGIN, "Access denied: admin role required");
  }

  @Test
  void isAdmin_ShouldReturnFalse_WhenNotAuthenticated() {
    // Given - no current user
    authService.logout();

    // When
    boolean result = authService.isAdmin();

    // Then
    assertThat(result).isFalse();
  }

  @Test
  void isAdmin_ShouldReturnFalse_WhenUserIsNotAdmin() {
    // Given
    User regularUser = createTestUser("user", Role.USER, "hash");
    setCurrentUser(regularUser);

    // When
    boolean result = authService.isAdmin();

    // Then
    assertThat(result).isFalse();
  }

  @Test
  void isAdmin_ShouldReturnTrue_WhenUserIsAdmin() {
    // Given
    User adminUser = createTestUser("admin", Role.ADMIN, "hash");
    setCurrentUser(adminUser);

    // When
    boolean result = authService.isAdmin();

    // Then
    assertThat(result).isTrue();
  }

  @Test
  void isAuthenticated_ShouldReturnFalse_WhenNoUser() {
    // Given - no current user
    authService.logout();

    // When
    boolean result = authService.isAuthenticated();

    // Then
    assertThat(result).isFalse();
  }

  @Test
  void isAuthenticated_ShouldReturnTrue_WhenUserExists() {
    // Given
    User user = createTestUser("user", Role.USER, "hash");
    setCurrentUser(user);

    // When
    boolean result = authService.isAuthenticated();

    // Then
    assertThat(result).isTrue();
  }

  @Test
  void logout_ShouldClearCurrentUser_WhenUserIsAuthenticated() {
    // Given
    User user = createTestUser("user", Role.USER, "hash");
    setCurrentUser(user);

    // When
    authService.logout();

    // Then
    assertThat(authService.isAuthenticated()).isFalse();
    assertThat(authService.getCurrentUser()).isEqualTo("unknown");
    verify(auditService).logAction("user", AuditAction.LOGOUT, "Logout successful");
  }

  @Test
  void getCurrentUser_ShouldReturnUnknown_WhenNotAuthenticated() {
    // Given - no current user
    authService.logout();

    // When
    String result = authService.getCurrentUser();

    // Then
    assertThat(result).isEqualTo("unknown");
  }

  @Test
  void getCurrentUser_ShouldReturnUsername_WhenAuthenticated() {
    // Given
    String username = "testuser";
    User user = createTestUser(username, Role.USER, "hash");
    setCurrentUser(user);

    // When
    String result = authService.getCurrentUser();

    // Then
    assertThat(result).isEqualTo(username);
  }

  @Test
  void getAdminUserName_ShouldReturnAdminConstant() {
    // When
    String result = authService.getAdminUserName();

    // Then
    assertThat(result).isEqualTo("admin");
  }

  @Test
  void requireAdmin_ShouldWorkAfterMultipleLogins() {
    // Given - login as regular user first
    User regularUser = createTestUser("user", Role.USER, "user_hash");
    when(userRepository.findByUsername("user")).thenReturn(regularUser);
    when(passwords.verifyPassword("pass", regularUser.getPasswordHash())).thenReturn(true);
    authService.login("user", "pass");

    // Then - should throw for regular user
    assertThatThrownBy(() -> authService.requireAdmin()).isInstanceOf(AccessDeniedException.class);

    // Given - now login as admin
    String username = "admin";
    User adminUser = createTestUser(username, Role.ADMIN, "admin_hash");
    when(userRepository.findByUsername(username)).thenReturn(adminUser);
    when(passwords.verifyPassword("adminpass", adminUser.getPasswordHash())).thenReturn(true);
    when(authLoginAttemptService.isAccountLocked(username)).thenReturn(false);
    authService.login(username, "adminpass");

    // Then - should not throw for admin
    assertThatNoException().isThrownBy(() -> authService.requireAdmin());
  }
}

package org.example.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.example.dto.LoginResult;
import org.example.exception.AccessDeniedException;
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
  private AuthLoginAttemptService authLoginAttemptService;

  private AuthServiceImpl authService;
  private Passwords passwords;

  @BeforeEach
  void setUp() {
    userRepository = Mockito.mock(UserRepository.class);
    passwords = Mockito.mock(PasswordsImpl.class);
    authLoginAttemptService = Mockito.mock(AuthLoginAttemptService.class);
    authService = new AuthServiceImpl(userRepository, authLoginAttemptService, passwords);
  }

  @Test
  void login_ShouldReturnFalse_WhenUsernameIsNull() {
    // When
    LoginResult result = authService.login(null, "password");

    // Then
    assertThat(result.isSuccess()).isFalse();
  }

  @Test
  void login_ShouldReturnFalse_WhenPasswordIsNull() {
    // When
    LoginResult result = authService.login("username", null);

    // Then
    assertThat(result.isSuccess()).isFalse();
  }

  @Test
  void login_ShouldReturnFalse_WhenUserNotFound() {
    // Given
    String username = "nonexistent";
    String password = "password";
    when(userRepository.findByUsername(username)).thenReturn(null);

    // When
    LoginResult result = authService.login(username, password);

    // Then
    assertThat(result.isSuccess()).isFalse();
    verify(userRepository).findByUsername(username);
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
    LoginResult result = authService.login(username, password);

    // Then
    assertThat(result.isSuccess()).isFalse();
    verify(userRepository).findByUsername(username);
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
    LoginResult result = authService.login(username, password);

    // Then
    assertThat(result.isSuccess()).isTrue();
    assertThat(authService.isAuthenticated()).isTrue();
    assertThat(authService.getCurrentUser().getUsername()).isEqualTo(username);
    verify(userRepository).findByUsername(username);
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
    assertThat(authService.getCurrentUser().getUsername()).isEqualTo(username);
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
    LoginResult result = authService.login(username, password);

    // Then
    assertThat(result.isSuccess()).isTrue();
    assertThat(authService.isAuthenticated()).isTrue();
    assertThat(authService.isAdmin()).isTrue();
    assertThat(authService.getCurrentUser().getUsername()).isEqualTo(username);
  }

  @Test
  void requireAdmin_ShouldNotThrow_WhenUserIsAdmin() {
    // Given
    User adminUser = createTestUser("admin", Role.ADMIN, "hash");
    UserContext.setCurrentUser(adminUser);

    // When & Then
    assertThatNoException().isThrownBy(() -> authService.requireAdmin());
  }

  @Test
  void requireAdmin_ShouldThrowAccessDenied_WhenUserIsNotAdmin() {
    // Given
    User regularUser = createTestUser("user", Role.USER, "hash");
    UserContext.setCurrentUser(regularUser);

    // When & Then
    assertThatThrownBy(() -> authService.requireAdmin())
        .isInstanceOf(AccessDeniedException.class)
        .hasMessage("Admin role required for this operation");
  }

  @Test
  void requireAdmin_ShouldThrowAccessDenied_WhenNotAuthenticated() {
    // Given - no current user
    authService.logout(); // Ensure no user

    // When & Then
    assertThatThrownBy(() -> authService.requireAdmin())
        .isInstanceOf(AccessDeniedException.class)
        .hasMessage("Admin role required for this operation");
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
    UserContext.setCurrentUser(regularUser);

    // When
    boolean result = authService.isAdmin();

    // Then
    assertThat(result).isFalse();
  }

  @Test
  void isAdmin_ShouldReturnTrue_WhenUserIsAdmin() {
    // Given
    User adminUser = createTestUser("admin", Role.ADMIN, "hash");
    UserContext.setCurrentUser(adminUser);

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
    UserContext.setCurrentUser(user);

    // When
    boolean result = authService.isAuthenticated();

    // Then
    assertThat(result).isTrue();
  }

  @Test
  void logout_ShouldClearCurrentUser_WhenUserIsAuthenticated() {
    // Given
    User user = createTestUser("user", Role.USER, "hash");
    UserContext.setCurrentUser(user);

    // When
    authService.logout();

    // Then
    assertThat(authService.isAuthenticated()).isFalse();
    assertThat(authService.getCurrentUser()).isEqualTo(User.anonymous());
  }

  @Test
  void getCurrentUser_ShouldReturnUnknown_WhenNotAuthenticated() {
    // Given - no current user
    authService.logout();

    // When
    User result = authService.getCurrentUser();

    // Then
    assertThat(result.getRole()).isEqualTo(Role.ANONYMOUS);
  }

  @Test
  void getCurrentUser_ShouldReturnUsername_WhenAuthenticated() {
    // Given
    String username = "testuser";
    User user = createTestUser(username, Role.USER, "hash");
    UserContext.setCurrentUser(user);

    // When
    User result = authService.getCurrentUser();

    // Then
    assertThat(result.getUsername()).isEqualTo(username);
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

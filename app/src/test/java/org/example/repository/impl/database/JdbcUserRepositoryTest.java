package org.example.repository.impl.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.example.model.Role;
import org.example.model.User;
import org.example_database.database.ConnectionManagerImpl;
import org.example_database.exception.DataAccessException;
import org.example_database.migration.LiquibaseConfigurationUpdater;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(
    classes = {
      JdbcUserRepository.class,
      ConnectionManagerImpl.class,
      LiquibaseConfigurationUpdater.class
    })
class JdbcUserRepositoryTest extends BaseRepositoryTest {
  @Autowired JdbcUserRepository userRepository;

  @Test
  void save_ShouldInsertNewUserAndReturnWithGeneratedId() {
    // Given
    User user = createTestUser("alice", "secure_hash", Role.ADMIN);

    // When
    User savedUser = userRepository.save(user);

    // Then
    assertThat(savedUser).isNotNull();
    assertThat(savedUser.getId()).isNotNull().isPositive();
    assertThat(savedUser.getUsername()).isEqualTo("alice");
    assertThat(savedUser.getPasswordHash()).isEqualTo("secure_hash");
    assertThat(savedUser.getRole()).isEqualTo(Role.ADMIN);
  }

  private User createTestUser(String username, String passwordHash, Role role) {
    User user = new User();
    user.setUsername(username);
    user.setPasswordHash(passwordHash);
    user.setRole(role);
    return user;
  }

  @Test
  void findByUsername_ShouldReturnNull_WhenUserDoesNotExist() {
    // When
    User foundUser = userRepository.findByUsername("nonexistent");

    // Then
    assertThat(foundUser).isNull();
  }

  @Test
  void findByUsername_ShouldReturnUser_WhenUserExists() {
    // Given
    User user = createTestUser("john.doe", "hashed_password", Role.USER);
    User savedUser = userRepository.save(user);

    // When
    User foundUser = userRepository.findByUsername("john.doe");

    // Then
    assertThat(foundUser).isNotNull();
    assertThat(foundUser.getId()).isEqualTo(savedUser.getId());
    assertThat(foundUser.getUsername()).isEqualTo("john.doe");
    assertThat(foundUser.getPasswordHash()).isEqualTo("hashed_password");
    assertThat(foundUser.getRole()).isEqualTo(Role.USER);
  }

  @Test
  void save_ShouldUpdateExistingUser() {
    // Given
    User user = createTestUser("bob", "old_hash", Role.USER);
    User savedUser = userRepository.save(user);

    // When - update the user
    savedUser.setUsername("bob_updated");
    savedUser.setPasswordHash("new_hash");
    savedUser.setRole(Role.ADMIN);
    User updatedUser = userRepository.save(savedUser);

    // Then
    assertThat(updatedUser.getId()).isEqualTo(savedUser.getId());
    assertThat(updatedUser.getUsername()).isEqualTo("bob_updated");
    assertThat(updatedUser.getPasswordHash()).isEqualTo("new_hash");
    assertThat(updatedUser.getRole()).isEqualTo(Role.ADMIN);

    // Verify the update persisted
    User foundUser = userRepository.findByUsername("bob_updated");
    assertThat(foundUser).isNotNull();
    assertThat(foundUser.getUsername()).isEqualTo("bob_updated");
    assertThat(foundUser.getRole()).isEqualTo(Role.ADMIN);
  }

  @Test
  void save_ShouldAllowUpdatingUsername_AndFindByNewUsername() {
    // Given
    User user = createTestUser("old_username", "hash", Role.USER);
    User savedUser = userRepository.save(user);

    // When - update username
    savedUser.setUsername("new_username");
    userRepository.save(savedUser);

    // Then - should find by new username, not old
    User byOldUsername = userRepository.findByUsername("old_username");
    User byNewUsername = userRepository.findByUsername("new_username");

    assertThat(byOldUsername).isNull();
    assertThat(byNewUsername).isNotNull();
    assertThat(byNewUsername.getId()).isEqualTo(savedUser.getId());
  }

  @Test
  void save_ShouldUpdatePasswordHash() {
    // Given
    User user = createTestUser("user", "old_password_hash", Role.USER);
    User savedUser = userRepository.save(user);

    // When - update password
    savedUser.setPasswordHash("new_password_hash");
    User updatedUser = userRepository.save(savedUser);

    // Then
    assertThat(updatedUser.getPasswordHash()).isEqualTo("new_password_hash");

    // Verify the password change persisted
    User foundUser = userRepository.findByUsername("user");
    assertThat(foundUser.getPasswordHash()).isEqualTo("new_password_hash");
  }

  @Test
  void save_ShouldUpdateUserRole() {
    // Given
    User user = createTestUser("moderator", "hash", Role.USER);
    User savedUser = userRepository.save(user);

    // When
    savedUser.setRole(Role.ADMIN);
    User updatedUser = userRepository.save(savedUser);

    // Then
    assertThat(updatedUser.getRole()).isEqualTo(Role.ADMIN);

    // Verify the role change persisted
    User foundUser = userRepository.findByUsername("moderator");
    assertThat(foundUser.getRole()).isEqualTo(Role.ADMIN);
  }

  @Test
  void save_ShouldThrowDataAccessException_WhenUpdateFails() {
    // Given
    User user = createTestUser("valid_user", "hash", Role.USER);
    User savedUser = userRepository.save(user);

    // When
    savedUser.setUsername(null);

    // Then
    assertThatThrownBy(() -> userRepository.save(savedUser))
        .isInstanceOf(DataAccessException.class)
        .hasMessageContaining("Failed to update user");
  }

  @Test
  void integrationTest_CompleteUserLifecycle() {
    // Given - Create user
    User user = createTestUser("testuser", "initial_hash", Role.USER);

    // When - Save user
    User savedUser = userRepository.save(user);

    // Then - Verify creation
    assertThat(savedUser.getId()).isNotNull();

    // When - Find by username
    User foundUser = userRepository.findByUsername("testuser");

    // Then - Verify found user
    assertThat(foundUser).isNotNull();
    assertThat(foundUser.getUsername()).isEqualTo("testuser");
    assertThat(foundUser.getRole()).isEqualTo(Role.USER);

    // When - Update user
    foundUser.setUsername("updated_user");
    foundUser.setPasswordHash("updated_hash");
    foundUser.setRole(Role.ADMIN);
    User updatedUser = userRepository.save(foundUser);

    // Then - Verify update
    assertThat(updatedUser.getUsername()).isEqualTo("updated_user");
    assertThat(updatedUser.getPasswordHash()).isEqualTo("updated_hash");
    assertThat(updatedUser.getRole()).isEqualTo(Role.ADMIN);

    // When - Find by new username
    User userByNewUsername = userRepository.findByUsername("updated_user");
    User userByOldUsername = userRepository.findByUsername("testuser");

    // Then - Verify username change
    assertThat(userByNewUsername).isNotNull();
    assertThat(userByOldUsername).isNull();
  }

  @Test
  void shouldHandleMultipleUsersWithDifferentRoles() {
    // Given
    User user1 = createTestUser("admin_user", "hash1", Role.ADMIN);
    User user2 = createTestUser("regular_user", "hash2", Role.USER);

    // When
    User saved1 = userRepository.save(user1);
    User saved2 = userRepository.save(user2);

    // Then
    User foundAdmin = userRepository.findByUsername("admin_user");
    User foundRegular = userRepository.findByUsername("regular_user");

    assertThat(foundAdmin).isNotNull();
    assertThat(foundAdmin.getRole()).isEqualTo(Role.ADMIN);

    assertThat(foundRegular).isNotNull();
    assertThat(foundRegular.getRole()).isEqualTo(Role.USER);

    // Verify all have different IDs
    assertThat(saved1.getId()).isNotEqualTo(saved2.getId());
  }
}

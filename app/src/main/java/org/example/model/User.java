package org.example.model;

import java.util.Objects;

public class User {
  private static final User ANONYMOUS = new User("ANONYMOUS", "", Role.ANONYMOUS);
  private Long id;
  private String username;
  private String passwordHash;
  private Role role;

  public User() {}

  public User(String username, String passwordHash, Role role) {
      this.username = username;
    this.passwordHash = passwordHash;
    this.role = role;
  }

  public static User anonymous() {
    return ANONYMOUS;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(username, user.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username);
  }

  @Override
  public String toString() {
    return String.format("User{username='%s',role=%s}", username, role);
  }
}

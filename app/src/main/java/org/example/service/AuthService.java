package org.example.service;

import org.example.dto.LoginResult;

/** Provides authentication and authorization methods */
public interface AuthService {
  /**
   * Make an attempt to login with given credentials if successful, isAuthenticated() will return
   * true afterward, unless logout() is called
   */
  LoginResult login(String username, String password);

  /**
   * Validation method to ensure current user has admin role Will throw AccessDeniedException if not
   * an admin
   */
  void requireAdmin();

  /** Information method if current user has admin role */
  boolean isAdmin();

  /** shows if there is an authenticated user currently in the session */
  boolean isAuthenticated();

  /**
   * remove authentication information for current user isAuthenticated will return false afterward
   */
  void logout();

  /** return username of current user, authenticated or not */
  String getCurrentUser();

  String getAdminUserName();
}

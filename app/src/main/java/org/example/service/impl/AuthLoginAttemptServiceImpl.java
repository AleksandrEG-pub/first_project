package org.example.service.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.example.service.AuthLoginAttemptService;
import org.springframework.stereotype.Component;

@Component
public class AuthLoginAttemptServiceImpl implements AuthLoginAttemptService {
  private static final int MAX_LOGIN_ATTEMPTS = 3;
  private static final int LOCKOUT_DURATION_MINUTES = 3;
  private final Map<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();

  @Override
  public boolean isAccountLocked(String username) {

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

  @Override
  public void remove(String username) {
    loginAttempts.remove(username);
  }

  @Override
  public void recordFailedAttempt(String username) {
    LoginAttempt attempt = loginAttempts.getOrDefault(username, new LoginAttempt());
    attempt.increment();
    loginAttempts.put(username, attempt);
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

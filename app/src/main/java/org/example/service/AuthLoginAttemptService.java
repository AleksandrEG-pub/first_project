package org.example.service;

/** Service for managing user login attempts and account lockout. */
public interface AuthLoginAttemptService {

    /** Checks if account is locked for the given username. */
    boolean isAccountLocked(String username);

    /** Records a failed login attempt for the user. */
    void recordFailedAttempt(String username);

    /** Removes login attempt records for the user (e.g., on successful login). */
    void remove(String username);
}

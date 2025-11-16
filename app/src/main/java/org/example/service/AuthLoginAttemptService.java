package org.example.service;

public interface AuthLoginAttemptService {

    boolean isAccountLocked(String username);

    void recordFailedAttempt(String username);

    void remove(String username);
}

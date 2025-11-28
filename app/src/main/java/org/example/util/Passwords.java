package org.example.util;

/** Service for password hashing and verification. */
public interface Passwords {

    /** Hashes a password for secure storage. */
    String hashPassword(String password);

    /** Verifies a password against a stored hash. */
    boolean verifyPassword(String password, String storedHash);
}

package org.example.util;

public interface Passwords {
    String hashPassword(String password);

    boolean verifyPassword(String password, String storedHash);
}

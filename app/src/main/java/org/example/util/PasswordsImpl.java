package org.example.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.example.exception.PasswordHashingException;
import org.example.exception.PasswordValidationException;
import org.springframework.stereotype.Component;

@Component
public class PasswordsImpl implements Passwords {
  private static final int MIN_PASSWORD_LENGTH = 8;
  private static final int SALT_LENGTH = 16;
  private static final int PBKDF2_ITERATIONS = 100000;
  private static final int KEY_LENGTH = 256;
  private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";

  /** Format: base64(salt):base64(hash) */
  @Override
  public String hashPassword(String password) {
    if (password == null) {
      throw new PasswordValidationException("Password cannot be null");
    }
    validatePasswordComplexity(password);
    try {
      SecureRandom random = new SecureRandom();
      byte[] salt = new byte[SALT_LENGTH];
      random.nextBytes(salt);

      PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH);
      SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
      byte[] hash = factory.generateSecret(spec).getEncoded();

      String saltBase64 = Base64.getEncoder().encodeToString(salt);
      String hashBase64 = Base64.getEncoder().encodeToString(hash);
      return saltBase64 + ":" + hashBase64;
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new PasswordHashingException("Failed to hash password", e);
    }
  }

  /**
 * @return true if password is valid
*/
  @Override
  public boolean verifyPassword(String password, String storedHash) {
    try {
      // Parse stored hash: format is salt:hash
      String[] parts = storedHash.split(":");
      if (parts.length != 2) {
        return false;
      }
      byte[] salt = Base64.getDecoder().decode(parts[0]);
      byte[] storedHashBytes = Base64.getDecoder().decode(parts[1]);
      // Hash the provided password with the same salt
      PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH);
      SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
      byte[] computedHash = factory.generateSecret(spec).getEncoded();

      return Arrays.equals(storedHashBytes, computedHash);
    } catch (Exception e) {
      return false;
    }
  }

  private void validatePasswordComplexity(String password) {
    if (password == null || password.length() < PasswordsImpl.MIN_PASSWORD_LENGTH) {
      throw new PasswordValidationException(
              "Password must be at least " + PasswordsImpl.MIN_PASSWORD_LENGTH + " characters long");
    }
    boolean hasUpperCase = false;
    boolean hasLowerCase = false;
    boolean hasDigit = false;
    boolean hasSpecialChar = false;
    for (char c : password.toCharArray()) {
      if (Character.isUpperCase(c)) {
        hasUpperCase = true;
      } else if (Character.isLowerCase(c)) {
        hasLowerCase = true;
      } else if (Character.isDigit(c)) {
        hasDigit = true;
      } else if (!Character.isLetterOrDigit(c)) {
        hasSpecialChar = true;
      }
    }
    if (!hasUpperCase) {
      throw new PasswordValidationException("Password must contain at least one uppercase letter");
    }
    if (!hasLowerCase) {
      throw new PasswordValidationException("Password must contain at least one lowercase letter");
    }
    if (!hasDigit) {
      throw new PasswordValidationException("Password must contain at least one digit");
    }
    if (!hasSpecialChar) {
      throw new PasswordValidationException("Password must contain at least one special character");
    }
  }
}

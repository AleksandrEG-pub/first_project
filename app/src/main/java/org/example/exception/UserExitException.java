package org.example.exception;

/**
 * Thrown when user-initiated exit occurs. Application exit is expected after this exception is
 * thrown. No user other user requests should be processed after this exception is thrown.
 * All resources should be cleaned up and released before exiting.
 */
public class UserExitException extends RuntimeException {
  public UserExitException(String message, Throwable cause) {
    super(message, cause);
  }
}

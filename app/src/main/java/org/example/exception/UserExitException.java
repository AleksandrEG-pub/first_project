package org.example.exception;

public class UserExitException extends RuntimeException {
  public UserExitException(String message, Throwable cause) {
    super(message, cause);
  }
}


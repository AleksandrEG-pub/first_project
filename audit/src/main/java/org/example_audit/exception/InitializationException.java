package org.example_audit.exception;

public class InitializationException extends RuntimeException {
  public InitializationException(String message) {
    super(message);
  }

  public InitializationException(Throwable cause) {
    super(cause);
  }
}

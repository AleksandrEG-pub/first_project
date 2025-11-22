package org.example.exception;

public class ApplicationException extends RuntimeException {
  public ApplicationException(String message, Throwable cause) {
    super(message, cause);
  }

  public ApplicationException(Throwable cause) {
    super(cause);
  }
}

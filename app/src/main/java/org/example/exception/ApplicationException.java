package org.example.exception;

public class ApplicationException extends RuntimeException {
  public ApplicationException(Throwable cause) {
    super(cause);
  }
}

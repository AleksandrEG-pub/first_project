package org.example.exception;

/**
* Must be thrown when user tries to perform operation they are not allowed to
*/
public class AccessDeniedException extends RuntimeException {
  public AccessDeniedException(String message) {
    super(message);
  }
}


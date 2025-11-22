package org.example.exception;

/** thrown for exceptions during application initialization before it starts serving requests */
public class InitializationException extends RuntimeException {
  public InitializationException(String message) {
    super(message);
  }

  public InitializationException(Throwable cause) {
    super(cause);
  }
}

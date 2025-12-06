package org.example.exception;

/**
 * General exception of the application.
 *
 * <p>Thrown in case of incorrect behavior. Can wrap closed resources exceptions,
 * NullPointersExceptions, Formatting exceptions etc.
 *
 * <p/> Expected to be caught by global exception handler with full stacktrace log
 */
public class ApplicationException extends RuntimeException {

  public ApplicationException(String message) {
    super(message);
  }

}

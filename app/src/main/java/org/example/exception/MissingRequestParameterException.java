package org.example.exception;

/** Thrown if http request is missing required parameter */
public class MissingRequestParameterException extends RuntimeException {
  public MissingRequestParameterException(String message) {
    super(message);
  }
}

package org.example.exception;

/** Thrown if http request's required parameter has wrong type. E.g. string instead if integer */
public class ParameterTypeMismatchException extends RuntimeException {
  public ParameterTypeMismatchException(String message, Throwable cause) {
    super(message, cause);
  }
}

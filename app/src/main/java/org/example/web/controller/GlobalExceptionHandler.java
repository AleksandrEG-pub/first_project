package org.example.web;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.example.exception.AccessDeniedException;
import org.example.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Log4j2
@Component
@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ResourceNotFoundException.class)
  public ErrorResponse handle(ResourceNotFoundException ex) {
    log.error("resource_not_found [{}]", ex.getMessage());
    String title = "resource_not_found: [%s, %s]".formatted(ex.getResource(), ex.getId());
    return errorResponse(ex, HttpStatus.NOT_FOUND, title);
  }

  private ErrorResponse errorResponse(Exception ex, HttpStatus httpStatus, String title) {
    ProblemDetail problemDetail = ProblemDetail.forStatus(httpStatus);
    return ErrorResponse.builder(ex, problemDetail).title(title).build();
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ErrorResponse handle(NoHandlerFoundException ex) {
    log.error("resource_does_not_exist [{}]", ex.getMessage());
    return errorResponse(
        ex, HttpStatus.NOT_FOUND, "resource_does_not_exist, %s".formatted(ex.getRequestURL()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ErrorResponse handle(MethodArgumentNotValidException ex) {
    log.error("incorrect_method_parameter_value [{}]", ex.getMessage());
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> "[%s:%s]".formatted(error.getField(), error.getDefaultMessage()))
            .collect(Collectors.joining(","));
    return errorResponse(
        ex, HttpStatus.BAD_REQUEST, "incorrect_method_parameter_value, %s".formatted(message));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ErrorResponse handle(ConstraintViolationException ex) {
    log.error("incorrect_parameter_value [{}]", ex.getMessage());
    String[] messageParts = ex.getMessage().split(":");
    String message = messageParts.length > 1 ? messageParts[1].trim() : ex.getMessage();
    return errorResponse(
        ex, HttpStatus.BAD_REQUEST, "incorrect_parameter_value, %s".formatted(message));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ErrorResponse handle(MissingServletRequestParameterException ex) {
    log.error("missing_parameter [{}]", ex.getMessage());
    return errorResponse(ex, HttpStatus.BAD_REQUEST, "missing_parameter, %s".formatted("message"));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ErrorResponse handle(MethodArgumentTypeMismatchException ex) {
    log.error("incorrect_parameter_type [{}]", ex.getMessage());
    return errorResponse(
        ex,
        HttpStatus.BAD_REQUEST,
        "incorrect_parameter_type, parameter: %s, value %s".formatted(ex.getName(), ex.getValue()));
  }

  @ExceptionHandler(ValidationException.class)
  public ErrorResponse handle(ValidationException ex) {
    log.error("validation_fail [{}]", ex.getMessage());
    return errorResponse(
        ex, HttpStatus.BAD_REQUEST, "validation_fail, [%s]".formatted(ex.getMessage()));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ErrorResponse handle(AccessDeniedException ex) {
    log.error("access_denied [{}]", ex.getMessage());
    return errorResponse(ex, HttpStatus.FORBIDDEN, "access_denied");
  }

  @ExceptionHandler(Exception.class)
  public ErrorResponse handle(Exception ex) {
    log.error(
        "Unexpected error happened: [{}, {}]", ex.getClass().getSimpleName(), ex.getMessage(), ex);
    return errorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, "internal_app_fail");
  }
}

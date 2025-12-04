package org.example.web.controller;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.AccessDeniedException;
import org.example.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@Component
@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ResourceNotFoundException.class)
  public ErrorResponse handle(ResourceNotFoundException ex) {
    return errorResponse(
        ex,
        HttpStatus.NOT_FOUND,
        "resource_not_found",
        "Resource [%s] not found by id: [%s]",
        ex.getResource(),
        ex.getId());
  }

  private ErrorResponse errorResponse(
      Exception ex,
      HttpStatus httpStatus,
      String title,
      String details,
      String... detailsArguments) {
    log.error("{} {} [{}]", title, ex.getClass().getSimpleName(), ex.getMessage());
    ProblemDetail problemDetail = ProblemDetail.forStatus(httpStatus);
    return ErrorResponse.builder(ex, problemDetail)
        .detail(details.formatted((Object [])detailsArguments))
        .title(title)
        .build();
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ErrorResponse handle(NoHandlerFoundException ex) {
    return errorResponse(
        ex,
        HttpStatus.NOT_FOUND,
        "resource_does_not_exist",
        "Resource does not exist for path %s",
        ex.getRequestURL());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ErrorResponse handle(MethodArgumentNotValidException ex) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> "[%s:%s]".formatted(error.getField(), error.getDefaultMessage()))
            .collect(Collectors.joining(","));
    return errorResponse(
        ex,
        HttpStatus.BAD_REQUEST,
        "incorrect_method_parameter_value",
        "wrong argument %s",
        message);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ErrorResponse handle(HttpRequestMethodNotSupportedException ex) {
    log.error("method_not_supported [{}]", ex.getMessage());
    return ex;
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ErrorResponse handle(ConstraintViolationException ex) {
    String[] messageParts = ex.getMessage().split(":");
    String message = messageParts.length > 1 ? messageParts[1].trim() : ex.getMessage();
    return errorResponse(
        ex, HttpStatus.BAD_REQUEST, "incorrect_parameter_value", "Validation fail [%s]", message);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ErrorResponse handle(MissingServletRequestParameterException ex) {
    return errorResponse(
        ex,
        HttpStatus.BAD_REQUEST,
        "missing_parameter",
        "expected parameter, but not given [%s, %s]",
        ex.getParameterName(),
        ex.getParameterType());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ErrorResponse handle(MethodArgumentTypeMismatchException ex) {
    return errorResponse(
        ex,
        HttpStatus.BAD_REQUEST,
        "incorrect_parameter_type",
        "Argument has wrong type: [%s, %s]",
        ex.getName(),
        ex.getMessage());
  }

  @ExceptionHandler(ValidationException.class)
  public ErrorResponse handle(ValidationException ex) {
    return errorResponse(
        ex,
        HttpStatus.BAD_REQUEST,
        "incorrect_parameter_value",
        "Validation fail [%s]",
        ex.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ErrorResponse handle(AccessDeniedException ex) {
    return errorResponse(
        ex, HttpStatus.FORBIDDEN, "access_denied", "ask administrator for additional details");
  }

  @ExceptionHandler(Exception.class)
  public ErrorResponse handle(Exception ex) {
    log.error(
        "Unexpected error happened: [{}, {}]", ex.getClass().getSimpleName(), ex.getMessage(), ex);
    return errorResponse(
        ex,
        HttpStatus.INTERNAL_SERVER_ERROR,
        "internal_app_fail",
        "Application experiencing problems, if error repeats, please report to developer team");
  }
}

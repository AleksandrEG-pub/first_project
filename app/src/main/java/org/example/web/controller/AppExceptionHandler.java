package org.example.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.exception.AccessDeniedException;
import org.example.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@Component
@ControllerAdvice
public class AppExceptionHandler {
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
        .detail(details.formatted((Object[]) detailsArguments))
        .title(title)
        .build();
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ErrorResponse handle(AccessDeniedException ex) {
    return errorResponse(
        ex, HttpStatus.FORBIDDEN, "access_denied", "ask administrator for additional details");
  }
}

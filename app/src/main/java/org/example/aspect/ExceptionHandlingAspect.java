package org.example.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import java.io.IOException;
import java.io.PrintWriter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.dto.ErrorResponse;
import org.example.exception.AccessDeniedException;
import org.example.exception.ApplicationException;
import org.example.exception.MissingRequestParameterException;
import org.example.exception.ParameterTypeMismatchException;
import org.example.exception.ResourceNotFoundException;

@Aspect
public class ExceptionHandlingAspect {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Around("execution(* org.example.web.servlet.*.do*(..))")
  public Object validateServlet(ProceedingJoinPoint jp) throws Throwable {
    try {
      return jp.proceed();
    } catch (ResourceNotFoundException e) {
      handleResourceNotFoundException(e, jp);
    } catch (ConstraintViolationException e) {
      handleConstraintViolationException(e, jp);
    } catch (ParameterTypeMismatchException e) {
      handleParameterTypeMismatchException(e, jp);
    } catch (MissingRequestParameterException e) {
      handleMissingRequestParameterException(e, jp);
    } catch (ValidationException e) {
      handleValidationException(e, jp);
    } catch (AccessDeniedException e) {
      handleAccessDeniedException(jp);
    } catch (Exception e) {
      handleException(e, jp);
    }
    return null;
  }

  private void handleResourceNotFoundException(
      ResourceNotFoundException e, ProceedingJoinPoint jp) {
    HttpServletResponse response = getResponse(jp);
    HttpServletRequest request = getRequest(jp);
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .type("resource_not_found type: [%s] id: [%s]".formatted(e.getResource(), e.getId()))
            .status(HttpServletResponse.SC_NOT_FOUND)
            .instance(toInstance(request))
            .build();
    sendErrorResponse(response, errorResponse);
  }

  private void handleConstraintViolationException(
      ConstraintViolationException e, ProceedingJoinPoint jp) {
    HttpServletResponse response = getResponse(jp);
    HttpServletRequest request = getRequest(jp);
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .type("wrong_parameter_value " + e.getMessage())
            .status(HttpServletResponse.SC_BAD_REQUEST)
            .instance(toInstance(request))
            .build();
    sendErrorResponse(response, errorResponse);
  }

  private void handleParameterTypeMismatchException(
      ParameterTypeMismatchException e, ProceedingJoinPoint jp) {
    HttpServletResponse response = getResponse(jp);
    HttpServletRequest request = getRequest(jp);
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .type("wrong_parameter " + e.getMessage())
            .status(HttpServletResponse.SC_BAD_REQUEST)
            .instance(toInstance(request))
            .build();
    sendErrorResponse(response, errorResponse);
  }

  private void handleMissingRequestParameterException(
      MissingRequestParameterException e, ProceedingJoinPoint jp) {
    HttpServletResponse response = getResponse(jp);
    HttpServletRequest request = getRequest(jp);
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .type("missing_parameter " + e.getMessage())
            .status(HttpServletResponse.SC_BAD_REQUEST)
            .instance(toInstance(request))
            .build();
    sendErrorResponse(response, errorResponse);
  }

  private void handleAccessDeniedException(ProceedingJoinPoint jp) {
    HttpServletResponse response = getResponse(jp);
    HttpServletRequest request = getRequest(jp);
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .type("access_denied")
            .status(HttpServletResponse.SC_FORBIDDEN)
            .instance(toInstance(request))
            .build();
    sendErrorResponse(response, errorResponse);
  }

  private void handleValidationException(ValidationException e, ProceedingJoinPoint jp) {
    HttpServletResponse response = getResponse(jp);
    HttpServletRequest request = getRequest(jp);
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .type("validation_exception " + e.getMessage())
            .status(HttpServletResponse.SC_BAD_REQUEST)
            .instance(toInstance(request))
            .build();
    sendErrorResponse(response, errorResponse);
  }

  private void handleException(Exception e, ProceedingJoinPoint jp) {
    HttpServletResponse response = getResponse(jp);
    HttpServletRequest request = getRequest(jp);
    e.printStackTrace();
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .type("server_error")
            .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
            .instance(toInstance(request))
            .build();
    sendErrorResponse(response, errorResponse);
  }

  private String toInstance(HttpServletRequest request) {
    return request.getRequestURI() + request.getContextPath();
  }

  private void sendErrorResponse(HttpServletResponse response, ErrorResponse errorResponse) {
    writeResponseJson(response, errorResponse, errorResponse.getStatus());
  }

  private void writeResponseJson(HttpServletResponse resp, Object object, int status) {
    resp.setContentType("application/json");
    resp.setStatus(status);
    String json = null;
    try {
      json = objectMapper.writer().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      writeToResponse(resp, "\"title\": \"server_error\"");
    }
    writeToResponse(resp, json);
  }

  private void writeToResponse(HttpServletResponse resp, String json) {
    try (PrintWriter writer = resp.getWriter()) {
      writer.write(json);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private HttpServletRequest getRequest(ProceedingJoinPoint jp) {
    Object[] args = jp.getArgs();
    for (Object arg : args) {
      if (arg instanceof HttpServletRequest request) {
        return request;
      }
    }
    throw new ApplicationException(
        "servlet join point does not not have HttpServletRequest parameter");
  }

  private HttpServletResponse getResponse(ProceedingJoinPoint jp) {
    Object[] args = jp.getArgs();
    for (Object arg : args) {
      if (arg instanceof HttpServletResponse response) {
        return response;
      }
    }
    throw new ApplicationException(
        "servlet join point does not not have HttpServletResponse parameter");
  }
}

package org.example.web.server.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.example.dto.ErrorResponse;
import org.example.model.Role;
import org.example.model.User;
import org.example.service.impl.UserContext;
import org.springframework.stereotype.Component;

/** Filter that enforces authorization rules for admin and product search access. */
@Component
@RequiredArgsConstructor
public class AuthorizationFilter implements Filter {
  private ObjectMapper objectMapper;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    User currentUser = UserContext.getValidatedCurrentUser();
    if (User.anonymous().equals(currentUser)) {
      sendUnauthorizedResponse(httpRequest, httpResponse, "unauthorized");
    }
    boolean notAdmin = !currentUser.getRole().equals(Role.ADMIN);
    if (!isProductSearchRequest(httpRequest) && notAdmin) {
      sendUnauthorizedResponse(httpRequest, httpResponse, "unauthorized");
      return;
    }
    chain.doFilter(request, response);
    UserContext.remove();
  }

  /** Sends 401 Unauthorized response with JSON error details. */
  private void sendUnauthorizedResponse(
      HttpServletRequest httpRequest, HttpServletResponse response, String message)
      throws IOException {
    response.setHeader("WWW-Authenticate", "Basic realm=\"User Visible Realm\"");
    response.setContentType("application/json");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    ErrorResponse errorResponse =
        getResponse(message, HttpServletResponse.SC_UNAUTHORIZED, httpRequest);
    String json = toJson(errorResponse);
    response.getWriter().write(json);
  }

  private ErrorResponse getResponse(String message, int status, HttpServletRequest httpRequest) {
    return ErrorResponse.builder()
        .status(status)
        .title("basic_authentication_error " + message)
        .instance(toInstance(httpRequest))
        .build();
  }

  private String toInstance(HttpServletRequest request) {
    return request.getContextPath() + request.getRequestURI();
  }

  private String toJson(Object object) throws JsonProcessingException {
    return objectMapper.writeValueAsString(object);
  }

  /** Checks if request is for product search (GET /products). */
  private static boolean isProductSearchRequest(HttpServletRequest httpRequest) {
    return httpRequest.getRequestURI().startsWith("/products")
        && "GET".equals(httpRequest.getMethod());
  }
}

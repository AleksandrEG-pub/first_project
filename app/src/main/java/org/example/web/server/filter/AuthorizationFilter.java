package org.example.web.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.example.model.Role;
import org.example.model.User;
import org.example.service.impl.UserContext;
import org.springframework.stereotype.Component;

/** Filter that enforces authorization rules for admin and product search access. */
@Component
public class AuthorizationFilter extends BasicFilter implements Filter {

  public AuthorizationFilter(ObjectMapper objectMapper) {
    super(objectMapper);
  }

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
    String json =
        getResponse(
            "basic_authentication_error",
            message,
            HttpServletResponse.SC_UNAUTHORIZED,
            httpRequest);
    response.getWriter().write(json);
  }

  /** Checks if request is for product search (GET /products). */
  private static boolean isProductSearchRequest(HttpServletRequest httpRequest) {
    return httpRequest.getRequestURI().startsWith("/products")
        && "GET".equals(httpRequest.getMethod());
  }
}

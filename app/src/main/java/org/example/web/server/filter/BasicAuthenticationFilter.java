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
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.example.dto.LoginResult;
import org.example.service.AuthService;
import org.springframework.stereotype.Component;

/** Filter that handles Basic Authentication for HTTP requests. */
@Component
public class BasicAuthenticationFilter extends BasicFilter implements Filter {

  private static final String AUTH_HEADER = "Authorization";
  private static final String BASIC_PREFIX = "Basic ";
  private final AuthService authService;

  public BasicAuthenticationFilter(AuthService authService, ObjectMapper objectMapper) {
    super(objectMapper);
    this.authService = authService;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    tryAuthenticate(chain, httpRequest, httpResponse);
  }

  /** Attempts to authenticate user using Basic Auth credentials. */
  private void tryAuthenticate(
      FilterChain chain, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
      throws IOException, ServletException {

    String authHeader = httpRequest.getHeader(AUTH_HEADER);
    if (authHeader == null || !authHeader.startsWith(BASIC_PREFIX)) {
      sendUnauthorizedResponse(
          httpRequest, httpResponse, "Missing or invalid Authorization header");
      return;
    }

    String[] credentials;
    try {
      credentials = extractCredentials(authHeader);
    } catch (IllegalArgumentException e) {
      sendUnauthorizedResponse(httpRequest, httpResponse, "Invalid Base64 encoding in credentials");
      return;
    }
    if (credentials.length != 2) {
      sendUnauthorizedResponse(httpRequest, httpResponse, "Invalid credential format");
      return;
    }

    String username = credentials[0];
    String password = credentials[1];
    LoginResult loginResult = authService.login(username, password);
    if (loginResult.isFailure()) {
      sendUnauthorizedResponse(httpRequest, httpResponse, loginResult.getMessage());
      return;
    }
    chain.doFilter(httpRequest, httpResponse);
  }

  /** Sends 401 response with Basic Auth challenge and JSON error. */
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

  /** Extracts username and password from Basic Auth header. */
  private String[] extractCredentials(String authHeader) {
    String base64Credentials = authHeader.substring(BASIC_PREFIX.length());
    byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
    String credentials = new String(credDecoded, StandardCharsets.UTF_8);
    return credentials.split(":", 2);
  }
}

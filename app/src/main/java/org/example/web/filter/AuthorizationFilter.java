package org.example.web.filter;

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

public class AuthorizationFilter implements Filter {

  private static final String AUTH_HEADER = "Authorization";
  private static final String BASIC_PREFIX = "Basic ";
  private static final String LOGIN_PATH = "/auth/login";
  private AuthService authService;

  public AuthorizationFilter(AuthService authService) {
    this.authService = authService;
  }

  public AuthorizationFilter() {}

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    if (isLoginRequest(httpRequest)) {
      chain.doFilter(request, response);
      return;
    }

    tryAuthenticate(chain, httpRequest, httpResponse);
  }

  private boolean isLoginRequest(HttpServletRequest request) {
    return LOGIN_PATH.equals(request.getRequestURI())
        || LOGIN_PATH.equals(request.getServletPath());
  }

  private void tryAuthenticate(
      FilterChain chain, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
      throws IOException, ServletException {

    String authHeader = httpRequest.getHeader(AUTH_HEADER);
    if (authHeader == null || !authHeader.startsWith(BASIC_PREFIX)) {
      sendUnauthorizedResponse(httpResponse, "Missing or invalid Authorization header");
      return;
    }

    String[] credentials;
    try {
      credentials = extractCredentials(authHeader);
    } catch (IllegalArgumentException e) {
      sendUnauthorizedResponse(httpResponse, "Invalid Base64 encoding in credentials");
      return;
    }
    if (credentials.length != 2) {
      sendUnauthorizedResponse(httpResponse, "Invalid credential format");
      return;
    }

    String username = credentials[0];
    String password = credentials[1];
    LoginResult loginResult = authService.login(username, password);
    if (loginResult.isFailure()) {
      sendUnauthorizedResponse(httpResponse, loginResult.getMessage());
      return;
    }
    chain.doFilter(httpRequest, httpResponse);
  }

  private void sendUnauthorizedResponse(HttpServletResponse response, String message)
      throws IOException {
    response.setHeader("WWW-Authenticate", "Basic realm=\"User Visible Realm\"");
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
  }

  private String[] extractCredentials(String authHeader) {
    String base64Credentials = authHeader.substring(BASIC_PREFIX.length());
    byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
    String credentials = new String(credDecoded, StandardCharsets.UTF_8);
    return credentials.split(":", 2);
  }
}

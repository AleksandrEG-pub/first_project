package org.example.web.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;

/** Filter that catches all exceptions and returns a JSON error response. */
@Log4j2
@Component
@RequiredArgsConstructor
public class GlobalExceptionFilter implements Filter {
  private final ObjectMapper objectMapper;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException {
    try {
      chain.doFilter(request, response);
    } catch (Exception e) {
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      log.error("global http exception [{}, {}]", e.getClass().getSimpleName(), e.getMessage(), e);
      if (!httpResponse.isCommitted()) {
        httpResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try (PrintWriter writer = httpResponse.getWriter()) {
          ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
          problemDetail.setInstance(URI.create(httpRequest.getRequestURI()));
          ErrorResponse errorResponse =
              ErrorResponse.builder(e, problemDetail)
                  .title("internal_server_error")
                  .detail(
                      "Application experiencing problems, if error repeats, please report to developer team")
                  .build();
          Map<String, Object> properties = errorResponse.getBody().getProperties();
          writer.write(objectMapper.writeValueAsString(properties));
        }
      }
    }
  }
}

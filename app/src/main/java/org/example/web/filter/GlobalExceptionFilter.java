package org.example.web.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/** Filter that catches all exceptions and returns a JSON error response. */
@Slf4j
@Component
public class GlobalExceptionFilter extends BasicFilter implements Filter {

  public GlobalExceptionFilter(ObjectMapper objectMapper) {
    super(objectMapper);
  }

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
          String json =
              getResponse(
                  "internal_server_error",
                  "Application experiencing problems, if error repeats, please report to developer team",
                  HttpStatus.INTERNAL_SERVER_ERROR.value(),
                  httpRequest);
          writer.write(json);
        }
      }
    }
  }
}

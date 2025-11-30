package org.example.web.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/** Filter that catches all exceptions and returns a JSON error response. */
public class GlobalExceptionFilter implements Filter {
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException {
    try {
      chain.doFilter(request, response);
    } catch (Exception e) {
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      e.printStackTrace();
      if (!httpResponse.isCommitted()) {
        httpResponse.setStatus(500);
        httpResponse.setContentType("application/json");
        try (PrintWriter writer = httpResponse.getWriter()) {
          String message =
              """
                      {
                      "type": "about:blank",
                      "title": "internal_server_error",
                      "status": 500,
                      "detail": "Application experiencing problems, if error repeats, please report to developer team"
                      }
                      """;
          writer.write(message);
        }
      }
    }
  }
}

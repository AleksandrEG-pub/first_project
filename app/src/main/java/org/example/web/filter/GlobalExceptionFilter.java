package org.example.web.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class GlobalExceptionFilter implements Filter {
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    try {
      chain.doFilter(request, response);
    } catch (Exception e) {
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      e.printStackTrace();
      if (!httpResponse.isCommitted()) {
        httpResponse.setStatus(500);
        httpResponse.setContentType("application/json");
        try(PrintWriter writer = httpResponse.getWriter()) {
          writer.write("{\"error\": \"internal_server_error\"}");
        }
      }
    }
  }
}

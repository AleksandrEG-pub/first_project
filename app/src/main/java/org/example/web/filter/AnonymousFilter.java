package org.example.web.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.example.model.User;
import org.example.service.impl.UserContext;

import java.io.IOException;

public class AnonymousFilter implements Filter {
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (UserContext.getCurrentUser() == null) {
      UserContext.setCurrentUser(User.anonymous());
    }
    chain.doFilter(request, response);
  }
}

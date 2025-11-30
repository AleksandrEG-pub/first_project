package org.example.web.server.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.example.model.User;
import org.example.service.impl.UserContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** Filter that sets anonymous user context when no user is present. */
@Component
public class AnonymousFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
          throws IOException, ServletException {
    if (UserContext.getCurrentUser().isEmpty()) {
      UserContext.setCurrentUser(User.anonymous());
    }
    chain.doFilter(request, response);
  }
}
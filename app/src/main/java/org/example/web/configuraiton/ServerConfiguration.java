package org.example.web.configuraiton;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.example.web.filter.AnonymousFilter;
import org.example.web.filter.AuthorizationFilter;
import org.example.web.filter.BasicAuthenticationFilter;
import org.example.web.filter.GlobalExceptionFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ServerConfiguration {

  private final AnonymousFilter anonymousFilter;
  private final AuthorizationFilter authorizationFilter;
  private final BasicAuthenticationFilter basicAuthenticationFilter;
  private final GlobalExceptionFilter globalExceptionFilter;

  @Bean
  public FilterRegistrationBean<AuthorizationFilter> authorizationFilterRegistration() {
    var reg = getFilterRegistrationBean(authorizationFilter);
    reg.setOrder(3);
    return reg;
  }

  @Bean
  public FilterRegistrationBean<BasicAuthenticationFilter> basicAuthenticationFilterFilterRegistration() {
    var reg = getFilterRegistrationBean(basicAuthenticationFilter);
    reg.setOrder(2);
    return reg;
  }

  @Bean
  public FilterRegistrationBean<AnonymousFilter> anonymousFilterRegistration() {
    var reg = getFilterRegistrationBean(anonymousFilter);
    reg.setOrder(1);
    return reg;
  }

  @Bean
  public FilterRegistrationBean<GlobalExceptionFilter> globalExceptionFilterFilterRegistration() {
    var reg = getFilterRegistrationBean(globalExceptionFilter);
    reg.setOrder(0);
    return reg;
  }

  private <T extends Filter> FilterRegistrationBean<T> getFilterRegistrationBean(T filter) {
    FilterRegistrationBean<T> registration = new FilterRegistrationBean<>();
    registration.setFilter(filter);
    registration.addUrlPatterns("/*");
    String name = filter.getClass().getSimpleName();
    registration.setName(name);
    return registration;
  }
}

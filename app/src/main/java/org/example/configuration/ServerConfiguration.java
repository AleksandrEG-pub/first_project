package org.example.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.example.exception.InitializationException;
import org.example.service.AuthService;
import org.example.web.filter.AnonymousFilter;
import org.example.web.filter.AuthorizationFilter;
import org.example.web.filter.BasicAuthenticationFilter;
import org.example.web.filter.GlobalExceptionFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
@RequiredArgsConstructor
public class ServerConfiguration {
  private final AuthService authService;
  private final ObjectMapper objectMapper;

  @Value("${server.port}")
  private int port;

  @Value("${server.hostname}")
  private String hostname;

  @Value("${server.base_dir}")
  private String baseDir;

  @Bean
  public ServletContext servletContext(Tomcat tomcat) {
    Container[] containers = tomcat.getHost().findChildren();
    if (containers.length == 0) {
      throw new InitializationException("failed to initialize tomcat, no tomcat context create");
    }
    Container container = containers[0];
    if (container instanceof StandardContext standardContext) {
      return new ApplicationContext(standardContext);
    } else {
      throw new InitializationException(
          "failed to initialize tomcat, wrong type of context: " + container.getClass());
    }
  }

  @Bean
  public DispatcherServlet dispatcherServlet() {
    DispatcherServlet dispatcherServlet = new DispatcherServlet();
    return dispatcherServlet;
  }

  @Bean
  public Tomcat tomcat(DispatcherServlet dispatcherServlet) {
    Tomcat tomcat = new Tomcat();
    tomcat.setHostname(hostname);
    tomcat.setPort(port);
    tomcat.setBaseDir(baseDir);
    Context tomcatContext = tomcat.addContext("", null);
    addServlet(tomcatContext, "/*", dispatcherServlet);
    addFilters(tomcatContext);
    return tomcat;
  }

  private void addServlet(Context ctx, String pathPattern, HttpServlet servlet) {
    String servletName = servlet.getClass().getSimpleName();
    Tomcat.addServlet(ctx, servletName, servlet);
    ctx.addServletMappingDecoded(pathPattern, servletName);
  }

  private void addFilters(Context ctx) {
    addGlobalExceptionFilter(ctx);
    addAnonymousFilter(ctx);
    addBasicAuthenticationFilter(ctx);
    addAuthorizationFilter(ctx);
  }

  private void addGlobalExceptionFilter(Context ctx) {
    GlobalExceptionFilter globalExceptionFilter = new GlobalExceptionFilter();
    addFilter(ctx, globalExceptionFilter, "/*");
  }

  private void addAnonymousFilter(Context ctx) {
    AnonymousFilter anonymousFilter = new AnonymousFilter();
    addFilter(ctx, anonymousFilter, "/*");
  }

  private void addBasicAuthenticationFilter(Context ctx) {
    BasicAuthenticationFilter basicAuthenticationFilter =
        new BasicAuthenticationFilter(authService, objectMapper);
    addFilter(ctx, basicAuthenticationFilter, "/*");
  }

  private void addAuthorizationFilter(Context ctx) {
    AuthorizationFilter authorizationFilter = new AuthorizationFilter(objectMapper);
    addFilter(ctx, authorizationFilter, "/*");
  }

  private void addFilter(Context ctx, Filter filter, String pattern) {
    FilterDef filterDef = new FilterDef();
    filterDef.setFilter(filter);
    String filterName = filter.getClass().getSimpleName();
    filterDef.setFilterName(filterName);
    ctx.addFilterDef(filterDef);
    FilterMap filterMap = new FilterMap();
    filterMap.setFilterName(filterName);
    filterMap.addURLPattern(pattern);
    ctx.addFilterMap(filterMap);
  }
}

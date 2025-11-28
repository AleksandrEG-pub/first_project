package org.example.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServlet;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.example.service.AuthService;
import org.example.web.configuration.ServletMapping;
import org.example.web.filter.AnonymousFilter;
import org.example.web.filter.AuthorizationFilter;
import org.example.web.filter.BasicAuthenticationFilter;
import org.example.web.filter.GlobalExceptionFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServerConfiguration {
  private final AuthService authService;
  private final ServletMapping servletMapping;
  private final ObjectMapper objectMapper;
  @Value("${server.port}")
  private int port;
  @Value("${server.hostname}")
  private String hostname;
  @Value("${server.base_dir}")
  private String baseDir;

  @PostConstruct
  private void init() throws LifecycleException {
    startServer();
  }

  public void startServer() throws LifecycleException {
    Tomcat tomcat = new Tomcat();
    tomcat.setHostname(hostname);
    tomcat.setPort(port);
    tomcat.setBaseDir(baseDir);
    Context ctx = tomcat.addContext("", null);

    addServlets(ctx);
    addFilters(ctx);

    tomcat.getConnector();
    tomcat.start();
    System.out.println("Server available at: http://localhost:" + port);
    tomcat.getServer().await();
  }

  private void addServlets(Context ctx) {
    servletMapping
        .getServletMapping()
        .forEach((path, httpServlet) -> addServlet(ctx, path, httpServlet));
  }

  private void addFilters(Context ctx) {
    addGlobalExceptionFilter(ctx);
    addAnonymousFilter(ctx);
    addBasicAuthenticationFilter(ctx);
    addAuthorizationFilter(ctx);
  }

  private void addServlet(Context ctx, String pathPattern, HttpServlet servlet) {
    String servletName = servlet.getClass().getSimpleName();
    Tomcat.addServlet(ctx, servletName, servlet);
    ctx.addServletMappingDecoded(pathPattern, servletName);
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

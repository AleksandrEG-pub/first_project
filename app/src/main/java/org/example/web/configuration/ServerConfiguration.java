package org.example.web.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.example.service.AuthService;
import org.example.web.filter.AnonymousFilter;
import org.example.web.filter.AuthorizationFilter;
import org.example.web.filter.BasicAuthenticationFilter;
import org.example.web.filter.GlobalExceptionFilter;

public class ServerConfiguration {
  private final AuthService authService;
  private final ServerConfigurationProperties serverConfigurationProperties;
  private final ServletMapping servletMapping;
  private final ObjectMapper objectMapper;

  public ServerConfiguration(
          AuthService authService,
          ServerConfigurationProperties serverConfigurationProperties,
          ServletMapping servletMapping, ObjectMapper objectMapper) {
    this.authService = authService;
    this.serverConfigurationProperties = serverConfigurationProperties;
    this.servletMapping = servletMapping;
      this.objectMapper = objectMapper;
  }

  public void startServer() throws LifecycleException {
    int port = serverConfigurationProperties.getPort();
    Tomcat tomcat = new Tomcat();
    tomcat.setHostname("localhost");
    tomcat.setPort(port);
    String baseDir = "./web-static";
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

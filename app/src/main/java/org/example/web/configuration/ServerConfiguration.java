package org.example.web.configuration;

import jakarta.servlet.http.HttpServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.example.service.AuthService;
import org.example.web.filter.AnonymousFilter;
import org.example.web.filter.AuthorizationFilter;

public class ServerConfiguration {
  private final AuthService authService;
  private final ServerConfigurationProperties serverConfigurationProperties;
  private final ServletMapping servletMapping;

  public ServerConfiguration(
      AuthService authService,
      ServerConfigurationProperties serverConfigurationProperties,
      ServletMapping servletMapping) {
    this.authService = authService;
    this.serverConfigurationProperties = serverConfigurationProperties;
    this.servletMapping = servletMapping;
  }

  public void startServer() throws LifecycleException {
    int port = serverConfigurationProperties.getPort();
    Tomcat tomcat = new Tomcat();
    tomcat.setHostname("localhost");
    tomcat.setPort(port);
    String baseDir = "./web-static";
    tomcat.setBaseDir(baseDir);

    Context ctx = tomcat.addContext("", null);
    servletMapping
        .getServletMapping()
        .forEach((path, httpServlet) -> addServlet(ctx, path, httpServlet));

    addAuthFilter(ctx);
    addAnonymousFilter(ctx);
    tomcat.getConnector();
    tomcat.start();
    System.out.println("Server available at: http://localhost:" + port);
    tomcat.getServer().await();
  }

  private void addServlet(Context ctx, String pathPattern, HttpServlet servlet) {
    String servletName = servlet.getClass().getSimpleName();
    Tomcat.addServlet(ctx, servletName, servlet);
    ctx.addServletMappingDecoded(pathPattern, servletName);
  }

  private void addAuthFilter(Context ctx) {
    FilterDef filterDef = new FilterDef();
    AuthorizationFilter authorizationFilter = new AuthorizationFilter(authService);
    filterDef.setFilter(authorizationFilter);
    String filterName = AuthorizationFilter.class.getSimpleName();
    filterDef.setFilterName(filterName);
    ctx.addFilterDef(filterDef);
    FilterMap filterMap = new FilterMap();
    filterMap.setFilterName(filterName);
    filterMap.addURLPattern("/*");
    ctx.addFilterMap(filterMap);
  }

  private void addAnonymousFilter(Context ctx) {
    FilterDef filterDef = new FilterDef();
    AnonymousFilter anonymousFilter = new AnonymousFilter();
    filterDef.setFilter(anonymousFilter);
    String filterName = AnonymousFilter.class.getSimpleName();
    filterDef.setFilterName(filterName);
    ctx.addFilterDef(filterDef);

    FilterMap filterMap = new FilterMap();
    filterMap.setFilterName(filterName);
    filterMap.addURLPattern("/*");
    ctx.addFilterMap(filterMap);
  }
}

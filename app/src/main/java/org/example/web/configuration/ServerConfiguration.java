package org.example.web.configuration;

import jakarta.servlet.http.HttpServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.example.web.filter.AuthorizationFilter;
import org.example.web.servlet.AuditServlet;
import org.example.web.servlet.AuthenticationServlet;
import org.example.web.servlet.ProductServlet;

public class ServerConfiguration {
  private final ServerConfigurationProperties serverConfigurationProperties;

  public ServerConfiguration(ServerConfigurationProperties serverConfigurationProperties) {
    this.serverConfigurationProperties = serverConfigurationProperties;
  }

  public void startServer() throws LifecycleException {
    int port = serverConfigurationProperties.getPort();
    Tomcat tomcat = new Tomcat();
    tomcat.setHostname("localhost");
    tomcat.setPort(port);
    String baseDir = "./web-static";
    tomcat.setBaseDir(baseDir);

    Context ctx = tomcat.addContext("", null);
    addServlet(ctx, "/products", new ProductServlet());
    addServlet(ctx, "/audits", new AuditServlet());
    addServlet(ctx, "/auth", new AuthenticationServlet());

    // Add filters
    addAuthFilter(ctx);
    tomcat.getConnector();
    tomcat.start();
    System.out.println("Server available at: http://localhost:" + port);
    tomcat.getServer().await();
  }

  private static void addServlet(Context ctx, String pathPattern, HttpServlet servlet) {
    String servletName = servlet.getClass().getSimpleName();
    Tomcat.addServlet(ctx, servletName, servlet);
    ctx.addServletMappingDecoded(pathPattern, servletName);
  }

  private static void addAuthFilter(Context ctx) {
    FilterDef filterDef = new FilterDef();
    filterDef.setFilterClass(AuthorizationFilter.class.getName());
    String authorizationFilter = "authorizationFilter";
    filterDef.setFilterName(authorizationFilter);
    ctx.addFilterDef(filterDef);
    FilterMap filterMap = new FilterMap();
    filterMap.setFilterName(authorizationFilter);
    filterMap.addURLPattern("/*");
    ctx.addFilterMap(filterMap);
  }
}

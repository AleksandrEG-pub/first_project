package org.example.web.configuration;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

public class ServerConfiguration {
  private final ServerConfigurationProperties serverConfigurationProperties;

    public ServerConfiguration(ServerConfigurationProperties serverConfigurationProperties) {
        this.serverConfigurationProperties = serverConfigurationProperties;
    }

    public void startServer() throws LifecycleException {
    int port = serverConfigurationProperties.getPort();
    Tomcat tomcat = new Tomcat();
    tomcat.setPort(port);
    tomcat.start();
    tomcat.getServer().await();
  }
}

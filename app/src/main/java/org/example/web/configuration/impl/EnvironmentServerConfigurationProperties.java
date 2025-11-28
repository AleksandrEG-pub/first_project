package org.example.web.configuration.impl;

import org.example.web.configuration.ServerConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentServerConfigurationProperties implements ServerConfigurationProperties {
  @Override
  public int getPort() {
    String portString = System.getProperty("YLAB_PROJECT_SERVER_PORT");
    try {
      return Integer.parseInt(portString);
    } catch (NumberFormatException e) {
      System.err.println("incorrect port value: " + portString);
      return 8080;
    }
  }
}

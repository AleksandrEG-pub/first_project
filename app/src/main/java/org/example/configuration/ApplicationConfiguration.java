package org.example.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.LifecycleException;
import org.example.exception.ApplicationException;
import org.example.web.configuration.ServerConfiguration;
import org.springframework.stereotype.Component;

/** Central application configuration and lifecycle coordinator. */
@Component
@RequiredArgsConstructor
public class ApplicationConfiguration {

  private final ServerConfiguration serverConfiguration;

  public void startServer() {
    try {
      serverConfiguration.startServer();
    } catch (LifecycleException e) {
      throw new ApplicationException(e);
    }
  }
}

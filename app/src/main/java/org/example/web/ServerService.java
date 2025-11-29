package org.example.web;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class ServerService {

  private final Tomcat tomcat;

  @Async
  public void start() throws LifecycleException {
    Connector connector = tomcat.getConnector();
    tomcat.start();
    log.info("Server available at: http://{}:{}", tomcat.getHost().getName(), connector.getPort());
    tomcat.getServer().await();
  }
}

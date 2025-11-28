package org.example.web;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServerStartListener implements ApplicationListener<ContextRefreshedEvent> {

  private final Tomcat tomcat;

  @SneakyThrows
  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    Connector connector = tomcat.getConnector();
    tomcat.start();
    System.out.printf(
        "Server available at: http://%s:%s%n", tomcat.getHost().getName(), connector.getPort());
    tomcat.getServer().await();
  }
}

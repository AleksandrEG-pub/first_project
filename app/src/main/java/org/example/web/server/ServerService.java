package org.example.web.server;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.springframework.stereotype.Component;

/**
* Starts the Tomcat server
 * Only 1 instance of server can be running at same time
*/
@Log4j2
@Component
@RequiredArgsConstructor
public class ServerService {

  private static final AtomicBoolean isServerStarted = new AtomicBoolean(false);
  private final Tomcat tomcat;

  public void start() {
    if (isServerStarted.compareAndSet(false, true)) {
      new Thread(
              () -> {
                try {
                  Connector connector = tomcat.getConnector();
                  tomcat.start();
                  log.info(
                      "Server available at: http://{}:{}",
                      tomcat.getHost().getName(),
                      connector.getPort());
                  tomcat.getServer().await();
                } catch (LifecycleException e) {
                  throw new RuntimeException(e);
                }
              })
          .start();
    }
  }
}

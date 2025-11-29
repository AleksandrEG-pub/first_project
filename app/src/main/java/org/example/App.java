package org.example;

import lombok.extern.log4j.Log4j2;
import org.example.configuration.ApplicationConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/** Application entry point. */
@Log4j2
public class App {
  public static void main(String[] args) {
    var applicationContext = new AnnotationConfigApplicationContext();
    applicationContext.register(ApplicationConfiguration.class);
    log.info("main application context refresh");
    applicationContext.refresh();
  }
}

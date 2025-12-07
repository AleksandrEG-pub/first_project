package org.example;

import org.example.configuration.ApplicationConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/** Application entry point. */
public class App {
  public static void main(String[] args) {
    var applicationContext = new AnnotationConfigApplicationContext();
    applicationContext.register(ApplicationConfiguration.class);
    applicationContext.refresh();
  }
}

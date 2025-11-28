package org.example;

import org.example.configuration.AppArgumentConfiguration;
import org.example.configuration.ApplicationConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/** Application entry point. */
public class App {
  /**
   * Configuration file specified with --file=/path/to/file
   *
   * @param args runtime arguments
   */
  public static void main(String[] args) {
    var applicationContext = new AnnotationConfigApplicationContext("org.example");
//    AppArgumentConfiguration.setConfigurationLocation(args);
//    ApplicationConfiguration appConfig = new ApplicationConfiguration();
//    appConfig.initializeData();
//    appConfig.startServer(appConfig.getServices());
  }
}

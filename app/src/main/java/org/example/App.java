package org.example;

import org.example.configuration.AppArgumentConfiguration;
import org.example.configuration.ApplicationConfiguration;

/** Application entry point. */
public class App {
  /**
   * <p>Configuration file specified with --file=/path/to/file
   *
   * @param args runtime arguments
   */
  public static void main(String[] args) {
    AppArgumentConfiguration.setConfigurationLocation(args);
    ApplicationConfiguration appConfig = new ApplicationConfiguration();
    appConfig.initializeData();
    appConfig.startServer(appConfig.getServices());
  }
}

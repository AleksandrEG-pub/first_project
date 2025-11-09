package org.example;

import org.example.configuration.ApplicationConfiguration;
import org.example.exception.UserExitException;

public class App {
  public static void main(String[] args) {
    ApplicationConfiguration appConfig = new ApplicationConfiguration();

    // Initialize default data and shutdown hook
    appConfig.initializeData();
    appConfig.registerShutdownHook();

    // Start the application
    try {
      appConfig.start();
    } catch (UserExitException e) {
      handleUserExit(appConfig);
    } finally {
      appConfig.shutdown();
    }
  }

  private static void handleUserExit(ApplicationConfiguration appConfig) {
    try {
      appConfig
          .getUi()
          .getConsoleUI()
          .printMessage("Exiting application (input closed or user requested exit). Goodbye.");
    } catch (Exception ignored) {
      // Application is exiting anyway
    }
  }
}

package org.example;

import org.example.configuration.ApplicationConfiguration;
import org.example.exception.UserExitException;

import java.util.Arrays;

/**
 * Application entry point.
 */
public class App {
  /**
   * Main entry. Accepts optional arguments; currently recognizes {@code --in-memory=true} which
   * will run the app without writing persistence files to disk.
   *
   * @param args runtime arguments
   */
  public static void main(String[] args) {
    boolean inMemory = Arrays.stream(args)
            .anyMatch(arg -> arg.contains("--in-memory=true"));
    ApplicationConfiguration appConfig = new ApplicationConfiguration(inMemory);

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

  /**
   * Attempt to print a friendly exit message to the console UI. This method intentionally swallows
   * exceptions because the application is exiting and there is nothing useful to recover.
   */
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

package org.example;

import java.io.IOException;
import org.example.configuration.ApplicationConfiguration;
import org.example.configuration.PropertiesFileReader;
import org.example.exception.InitializationException;
import org.example.exception.UserExitException;

/** Application entry point. */
public class App {
  /**
   * Main entry. Accepts optional arguments; currently recognizes
   * --repository-type=[in-memory|file|database] in-memory - will run the app without writing
   * persistence files to disk file - persistence is written to files to disk in directory ./data
   * database - persistence based on SQL database
   *
   * Configuration file specified with --file=/path/to/file
   *
   * @param args runtime arguments
   */
  public static void main(String[] args) {
    setConfigurationLocation(args);
    ApplicationConfiguration appConfig = new ApplicationConfiguration();
    try {
      appConfig.initializeData();
      appConfig.start();
    } catch (UserExitException e) {
      handleUserExit(appConfig);
    } finally {
      appConfig.shutdown();
    }
  }

  public static void setConfigurationLocation(String[] args) {
    for (String arg : args) {
      if (arg.startsWith("--file=")) {
        String[] parts = arg.split("=", 2);
        if (parts.length == 2 && !parts[1].trim().isEmpty()) {
          try {
            PropertiesFileReader.readEnvFile(parts[1].trim());
          } catch (IOException e) {
            throw new InitializationException("failed to read configuration file");
          }
        }
      }
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

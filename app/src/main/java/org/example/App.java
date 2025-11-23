package org.example;

import org.example.configuration.AppArgumentConfiguration;
import org.example.configuration.ApplicationConfiguration;

/** Application entry point. */
public class App {
  /**
   * Main entry. Accepts optional arguments; currently recognizes
   * --repository-type=[in-memory|file|database] in-memory - will run the app without writing
   * persistence files to disk file - persistence is written to files to disk in directory ./data
   * database - persistence based on SQL database
   *
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

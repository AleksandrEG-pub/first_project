package org.example;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.configuration.ApplicationConfiguration;
import org.example.configuration.RepositoryType;
import org.example.exception.UserExitException;

/** Application entry point. */
public class App {
  /**
   * Main entry.
   * Accepts optional arguments; currently recognizes
   * --repository-type=[in-memory|file|database]
   * in-memory - will run the app without writing persistence files to disk
   * file - persistence is written to files to disk in directory ./data
   * database - persistence based on SQL database
   *
   * @param args runtime arguments
   */
  public static void main(String[] args) {
    RepositoryType repositoryType = getRepositoryType(args);
    ApplicationConfiguration appConfig = new ApplicationConfiguration(repositoryType);
    try {
      appConfig.initializeData();
      appConfig.start();
    } catch (UserExitException e) {
      handleUserExit(appConfig);
    } finally {
      appConfig.shutdown();
    }
  }

  private static RepositoryType getRepositoryType(String[] args) {
    if (args == null || args.length == 0) {
      return RepositoryType.IN_MEMORY;
    }
    Set<String> argumentSet = Arrays.stream(args).collect(Collectors.toSet());
    RepositoryType repositoryType;
    if (argumentSet.contains("--repository-type=database")) {
      repositoryType = RepositoryType.DATABASE;
    } else if (argumentSet.contains("--repository-type=file")) {
      repositoryType = RepositoryType.FILE;
    } else if (argumentSet.contains("--repository-type=in-memory")) {
      repositoryType = RepositoryType.IN_MEMORY;
    } else {
      repositoryType = RepositoryType.IN_MEMORY;
    }
    return repositoryType;
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

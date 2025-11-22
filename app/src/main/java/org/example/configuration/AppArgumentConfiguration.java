package org.example.configuration;

import java.io.IOException;
import org.example.exception.InitializationException;

public class AppArgumentConfiguration {

  private AppArgumentConfiguration() {}

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
}

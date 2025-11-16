package org.example.configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PropertiesFileReader {

  /** Reads file from given location. For every found property set it to System as property */
  public static void readEnvFile(String filePath) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty() || line.startsWith("#")) {
          continue;
        }

        int equalsIndex = line.indexOf('=');
        if (equalsIndex > 0) {
          String key = line.substring(0, equalsIndex).trim();
          String value = line.substring(equalsIndex + 1).trim();
          System.setProperty(key, value);
        }
      }
    }
  }
}

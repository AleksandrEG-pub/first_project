package org.example.configuration;

import java.util.HashMap;
import java.util.Map;
import org.example.exception.InitializationException;

public class EnvDatabaseProperties implements DatabaseProperties {
  private final String url;
  private final String user;
  private final String password;

  public EnvDatabaseProperties() {
    String baseUrl = System.getProperty("YLAB_PROJECT_POSTGRES_URL");
    String scheme = System.getProperty("YLAB_PROJECT_APPLICATION_SCHEME");
    user = System.getProperty("YLAB_PROJECT_POSTGRES_USER");
    password = System.getProperty("YLAB_PROJECT_POSTGRES_PASSWORD");
    HashMap<String, String> propertyMap = new HashMap<>();
    propertyMap.put("baseUrl", baseUrl);
    propertyMap.put("scheme", scheme);
    propertyMap.put("user", user);
    propertyMap.put("password", password);
    validateProperties(propertyMap);
    url = "%s?currentSchema=%s".formatted(baseUrl, scheme);
  }

  private void validateProperties(Map<String, String> properties) {
    for (Map.Entry<String, String> entry : properties.entrySet()) {
      if (entry.getValue() == null || entry.getValue().trim().isEmpty()) {
        throw new InitializationException(
            "Property '" + entry.getKey() + "' cannot be null or empty");
      }
    }
  }

  public String getUrl() {
    return url;
  }

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }
}

package org.example.configuration;

import org.example.exception.InitializationException;

public class EnvDatabaseProperties implements DatabaseProperties {
  private final String url;
  private final String user;
  private final String password;

  public EnvDatabaseProperties() {
    String baseUrl = System.getenv("YLAB_PROJECT_POSTGRES_URL");
    String scheme = System.getenv("YLAB_PROJECT_APPLICATION_SCHEME");
    user = System.getenv("YLAB_PROJECT_POSTGRES_USER");
    password = System.getenv("YLAB_PROJECT_POSTGRES_PASSWORD");
    if (baseUrl == null || scheme == null || user == null || password == null) {
      throw new InitializationException(
          "Incorrect database configurations in environment variables");
    }
    url = "%s?currentSchema=%s".formatted(baseUrl, scheme);
  }

  @Override
  public String getUrl() {
    return url;
  }

  @Override
  public String getUser() {
    return user;
  }

  @Override
  public String getPassword() {
    return password;
  }
}

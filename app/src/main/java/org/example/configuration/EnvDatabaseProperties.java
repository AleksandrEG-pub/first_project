package org.example.configuration;

import java.util.stream.Stream;
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
    validateProperties(baseUrl, scheme);
    url = "%s?currentSchema=%s".formatted(baseUrl, scheme);
  }

  private void validateProperties(String baseUrl, String scheme) {
    Stream.of(baseUrl, scheme, user, password)
        .filter(this::isNullOfEmpty)
        .findAny()
        .ifPresent(
            s -> {
              throw new InitializationException(
                  "Incorrect database configurations in environment variables");
            });
  }

  private boolean isNullOfEmpty(String value) {
    return value == null || value.isBlank();
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

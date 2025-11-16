package org.example.configuration;

import org.example.exception.InitializationException;

public class LiquibaseConfiguration {
  /** liquibase configuration file location */
  private final String changelogFile;
  /** url of database to connect */
  private final String url;
  /** user to connect to database */
  private final String username;
  /** password to connect to database */
  private final String password;

  /** name of database scheme with liquibase tables */
  private final String liquibaseScheme;
  /** name of database scheme with application tables and data */
  private final String applicationScheme;

  private LiquibaseConfiguration(Builder builder) {
    this.changelogFile = builder.changelogFile;
    this.url = builder.url;
    this.username = builder.username;
    this.password = builder.password;
    this.liquibaseScheme = builder.liquibaseScheme;
    this.applicationScheme = builder.applicationScheme;
  }

  public String getUrl() {
    return url;
  }

  public String getPassword() {
    return password;
  }

  public String getUsername() {
    return username;
  }

  public String getApplicationScheme() {
    return applicationScheme;
  }

  public String getChangelogFile() {
    return changelogFile;
  }

  public String getLiquibaseScheme() {
    return liquibaseScheme;
  }

  public static class Builder {
    private String changelogFile = "db/changelog/db.changelog-master.yaml";
    private String url;
    private String username;
    private String password;
    private String liquibaseScheme = "public";
    private String applicationScheme = "public";

    public Builder withChangelogFile(String changelogFile) {
      this.changelogFile = changelogFile;
      return this;
    }

    public Builder withUrl(String url) {
      this.url = url;
      return this;
    }

    public Builder withUsername(String username) {
      this.username = username;
      return this;
    }

    public Builder withPassword(String password) {
      this.password = password;
      return this;
    }

    public Builder withLiquibaseScheme(String scheme) {
      this.liquibaseScheme = scheme;
      return this;
    }

    public Builder withApplicationScheme(String scheme) {
      this.applicationScheme = scheme;
      return this;
    }

    public Builder fromEnvironment() {
      this.url = System.getenv("YLAB_PROJECT_POSTGRES_URL");
      this.username = System.getenv("YLAB_PROJECT_POSTGRES_USER");
      this.password = System.getenv("YLAB_PROJECT_POSTGRES_PASSWORD");
      this.liquibaseScheme = System.getenv("YLAB_PROJECT_LIQUIBASE_SCHEME");
      this.applicationScheme = System.getenv("YLAB_PROJECT_APPLICATION_SCHEME");
      return this;
    }

    public LiquibaseConfiguration build() {
      validateProperty(changelogFile, "changelogFile must be provided");
      validateProperty(url, "url must be provided");
      validateProperty(username, "username must be provided");
      validateProperty(password, "password must be provided");
      validateProperty(liquibaseScheme, "liquibaseScheme must be provided");
      validateProperty(applicationScheme, "applicationScheme must be provided");
      validateScheme(liquibaseScheme);
      validateScheme(applicationScheme);
      return new LiquibaseConfiguration(this);
    }

    private void validateProperty(String property, String message) {
      if (property == null || property.isBlank()) {
        throw new InitializationException(message);
      }
    }

    private void validateScheme(String scheme) {
      if (!scheme.matches("[a-zA-Z_]{0,20}")) {
        throw new InitializationException("Invalid schema name");
      }
    }
  }
}

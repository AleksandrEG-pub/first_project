package org.example.configuration;

/** Holds main properties for jdbc connection */
public interface DatabaseProperties {
  String getUrl();

  String getUser();

  String getPassword();
}

package org.example.configuration;

/** Holds main properties for jdbc connection */
public interface DatabaseProperties {
  /** url of database */
  String getUrl();
  /** user of database */
  String getUser();
  /** user's password to access database */
  String getPassword();
}

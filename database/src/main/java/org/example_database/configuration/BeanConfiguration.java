package org.example_database.configuration;

import org.example_database.database.ConnectionManager;
import org.example_database.database.ConnectionManagerImpl;
import org.example_database.migration.LiquibaseConfigurationUpdater;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Database module application context beans */
@Configuration
public class BeanConfiguration {

  @Bean
  @ConditionalOnMissingBean(ConnectionManager.class)
  public ConnectionManager connectionManager() {
    return new ConnectionManagerImpl();
  }

  @Bean
  @ConditionalOnMissingBean(LiquibaseConfigurationUpdater.class)
  public LiquibaseConfigurationUpdater liquibaseConfigurationUpdater() {
    return new LiquibaseConfigurationUpdater();
  }
}

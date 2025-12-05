package org.example_database.configuration;

import org.example_database.database.ConnectionManager;
import org.example_database.database.ConnectionManagerImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

  @Bean
  @ConditionalOnMissingBean(ConnectionManager.class)
  public ConnectionManager connectionManager() {
    return new ConnectionManagerImpl();
  }
}

package org.example_logging.configuration;

import org.example_logging.aspect.ExecutionTimeRepositoryAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
  @Bean
  public ExecutionTimeRepositoryAspect executionTimeRepositoryAspect() {
    return new ExecutionTimeRepositoryAspect();
  }
}

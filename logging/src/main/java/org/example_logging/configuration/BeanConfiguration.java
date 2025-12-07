package org.example_logging.configuration;

import org.example_logging.aspect.ExecutionTimeAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class BeanConfiguration {
  @Bean
  public ExecutionTimeAspect executionTimeRepositoryAspect() {
    return new ExecutionTimeAspect();
  }
}

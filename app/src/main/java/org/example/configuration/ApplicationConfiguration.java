package org.example.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.cache.Cache;
import org.example.cache.ProductBaseCache;
import org.example.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@ComponentScan(
    basePackages = "org.example",
    excludeFilters =
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org\\.example\\.web\\..*"))
@EnableAspectJAutoProxy
@EnableAsync
public class ApplicationConfiguration {

  @Value("${cache.product.size}")
  private int cacheSize;

  @Bean
  public ObjectMapper objectMapper() {
    var objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  @Bean
  public Validator validator() {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      return factory.getValidator();
    }
  }

  @Bean
  public Cache<Long, Product> cache() {
    return new ProductBaseCache(cacheSize);
  }
}

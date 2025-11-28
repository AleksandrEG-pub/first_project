package org.example.configuration;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import org.example.exception.InitializationException;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

public class YamlPropertySourceFactory implements PropertySourceFactory {
  @Override
  public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) {
    YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
    Resource resource = encodedResource.getResource();
    factory.setResources(resource);
    Properties properties = factory.getObject();
    if (properties == null) {
      properties = new Properties();
    }
    validateProperties(properties);

    String filename = resource.getFilename();
    if (filename == null) {
      filename = UUID.randomUUID().toString();
    }
    return new PropertiesPropertySource(filename, properties);
  }

  private void validateProperties(Properties properties) {
    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
      if (entry.getValue() instanceof String value) {
        if (value.trim().isBlank()) {
          throw new InitializationException(
              "Property [%s] can not be empty".formatted(entry.getKey()));
        }
      }
    }
  }
}

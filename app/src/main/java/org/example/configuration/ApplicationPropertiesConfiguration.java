package org.example.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/** Locates and converts properties from yaml files to application environment */
@Configuration
@PropertySource(value = "classpath:application.yaml", factory = YamlPropertySourceFactory.class)
public class ApplicationPropertiesConfiguration {}

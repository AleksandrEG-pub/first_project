package org.example.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;

/**
 * Contains configuration for main application context with services and repositories.
 * <p/>
 * Does not include web application related beans.
 * */
@Configuration
@ComponentScan(
    basePackages = "org.example",
    excludeFilters =
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org\\.example\\.web\\..*"))
@EnableAspectJAutoProxy
public class ApplicationConfiguration {}

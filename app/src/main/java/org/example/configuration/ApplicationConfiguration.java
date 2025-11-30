package org.example.configuration;

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
public class ApplicationConfiguration {}

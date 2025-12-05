package org.example;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/** Application entry point. */
@SpringBootApplication
public class App {
  public static void main(String[] args) {
    SpringApplication application =
        new SpringApplicationBuilder(App.class).bannerMode(Banner.Mode.OFF).build();
    application.run(args);
  }
}

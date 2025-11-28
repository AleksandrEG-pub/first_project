package org.example;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/** Application entry point. */
public class App {
  public static void main(String[] args) {
    var applicationContext = new AnnotationConfigApplicationContext();
    applicationContext.scan(App.class.getPackageName());
    applicationContext.refresh();
  }
}

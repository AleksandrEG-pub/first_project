package org.example.configuration;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import org.springframework.stereotype.Component;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@Component
public class MyWebApplicationInitializer implements WebApplicationInitializer {

  @Override
  public void onStartup(ServletContext container) {
    var webContext = new AnnotationConfigWebApplicationContext();
    webContext.setParent(webContext);
    webContext.scan("org.example.web");

    ServletRegistration.Dynamic registration =
        container.addServlet("dispatcher", new DispatcherServlet(webContext));
    registration.setLoadOnStartup(1);
    registration.addMapping("/");
  }
}

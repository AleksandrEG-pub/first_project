package org.example.configuration;

import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.startup.Tomcat;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@Component
@RequiredArgsConstructor
public class WebApplicationConfiguration {

  private final Tomcat tomcat;
  private final DispatcherServlet dispatcherServlet;

  @EventListener
  public void initWebContext(ContextRefreshedEvent contextRefreshedEvent) {
    ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
    var webContext = new AnnotationConfigWebApplicationContext();
    webContext.setParent(applicationContext);
    webContext.scan("org.example.web");
    ServletContext servletContext = applicationContext.getBean(ServletContext.class);
    webContext.setServletContext(servletContext);
    DispatcherServlet dispatcherServlet = applicationContext.getBean(DispatcherServlet.class);
    dispatcherServlet.setApplicationContext(webContext);
    dispatcherServlet.setContextAttribute("my-ds");
    servletContext.setAttribute("my-ds", webContext);
    webContext.refresh();
  }
}

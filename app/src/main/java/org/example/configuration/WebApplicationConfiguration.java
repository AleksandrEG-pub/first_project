package org.example.configuration;

import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.startup.Tomcat;
import org.example.web.ServerStartListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
@RequiredArgsConstructor
public class WebApplicationConfiguration {

  private final Tomcat tomcat;
  private final ServerStartListener serverStartListener;

  @EventListener
  public void init(ContextRefreshedEvent contextRefreshedEvent) {
    ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
    var webContext = new AnnotationConfigWebApplicationContext();
    webContext.setParent(applicationContext);
    webContext.scan("org.example.web");
    webContext.addApplicationListener(serverStartListener);
    ServletContext servletContext = applicationContext.getBean(ServletContext.class);
    webContext.setServletContext(servletContext);
    DispatcherServlet dispatcherServlet = applicationContext.getBean(DispatcherServlet.class);
    dispatcherServlet.setApplicationContext(webContext);
    webContext.refresh();
  }
}

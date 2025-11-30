package org.example.configuration;

import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import org.example.web.configuration.WebMvcConfiguration;
import org.example.web.server.ServerConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@Component
@RequiredArgsConstructor
public class WebApplicationConfiguration {

  @EventListener
  public void initWebContext(ContextRefreshedEvent contextRefreshedEvent) {
    // Only proceed if this is the root/parent application context
    if (contextRefreshedEvent.getApplicationContext().getParent() != null) {
      return;
    }
    ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();

    var serverContext = new AnnotationConfigApplicationContext();
    serverContext.register(ServerConfiguration.class);
    serverContext.scan("org.example.web.server");
    serverContext.setParent(applicationContext);
    serverContext.setEnvironment((ConfigurableEnvironment) applicationContext.getEnvironment());
    serverContext.refresh();

    var webContext = new AnnotationConfigWebApplicationContext();
    webContext.setParent(serverContext);
    webContext.register(WebMvcConfiguration.class);
    webContext.scan("org.example.web.controller");
    ServletContext servletContext = serverContext.getBean(ServletContext.class);
    webContext.setServletContext(servletContext);
    webContext.setEnvironment((ConfigurableEnvironment) applicationContext.getEnvironment());
    DispatcherServlet dispatcherServlet = serverContext.getBean(DispatcherServlet.class);
    dispatcherServlet.setApplicationContext(webContext);
    dispatcherServlet.setContextAttribute("my-ds");
    servletContext.setAttribute("my-ds", webContext);
    webContext.refresh();
  }
}

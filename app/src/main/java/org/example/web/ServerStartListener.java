package org.example.web;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServerStartListener {

  private final ServerService serverService;

  @SneakyThrows
  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event) {
    serverService.start();
  }
}

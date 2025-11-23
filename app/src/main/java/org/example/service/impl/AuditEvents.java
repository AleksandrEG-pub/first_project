package org.example.service.impl;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.example.dto.AuditEvent;

public class AuditEvents {
  private static Map<String, Consumer<AuditEvent>> listeners = new ConcurrentHashMap<>();

  public static String addListener(Consumer<AuditEvent> listener) {
    UUID uuid = UUID.randomUUID();
    listeners.put(uuid.toString(), listener);
    return uuid.toString();
  }

  public static void publish(AuditEvent auditEvent) {
    listeners.values().forEach(auditEventConsumer -> auditEventConsumer.accept(auditEvent));
  }
}

package org.example.configuration;

import org.example.repository.impl.in_memory.InMemoryAuditRepository;
import org.example.repository.impl.in_memory.InMemoryProductRepository;
import org.example.repository.impl.in_memory.InMemoryUserRepository;

public class InMemoryServiceConfiguration extends ServiceConfiguration {

  public InMemoryServiceConfiguration() {
    super(
        new InMemoryProductRepository(),
        new InMemoryUserRepository(),
        new InMemoryAuditRepository());
  }
}

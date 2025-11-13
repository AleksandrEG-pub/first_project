package org.example.configuration;

import org.example.repository.InMemoryAuditRepository;
import org.example.repository.InMemoryProductRepository;
import org.example.repository.InMemoryUserRepository;

public class InMemoryServiceConfiguration extends ServiceConfiguration {

  public InMemoryServiceConfiguration() {
    super(
        new InMemoryProductRepository(),
        new InMemoryUserRepository(),
        new InMemoryAuditRepository());
  }
}

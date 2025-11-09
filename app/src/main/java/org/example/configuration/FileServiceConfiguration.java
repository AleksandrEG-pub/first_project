package org.example.configuration;

import java.io.File;

import org.example.console.ui.ConsoleUI;
import org.example.repository.FileAuditRepository;
import org.example.repository.FileProductRepository;
import org.example.repository.FileUserRepository;

public class FileServiceConfiguration extends ServiceConfiguration {

  public FileServiceConfiguration(ConsoleUI ui) {
    this(ui, new File("data/products.csv"), new File("data/users.csv"), new File("data/audit.csv"));
  }

  public FileServiceConfiguration(ConsoleUI ui, File productsFile, File usersFile, File auditFile) {
    super(
        new FileProductRepository(ui, productsFile),
        new FileUserRepository(ui, usersFile),
        new FileAuditRepository(ui, auditFile));
  }

  // Optional: Provide access to specific file repositories if needed
  public FileProductRepository getFileProductRepository() {
    return (FileProductRepository) productRepository;
  }

  public FileUserRepository getFileUserRepository() {
    return (FileUserRepository) userRepository;
  }

  public FileAuditRepository getFileAuditRepository() {
    return (FileAuditRepository) auditRepository;
  }
}

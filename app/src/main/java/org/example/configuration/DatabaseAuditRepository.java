package org.example.configuration;

import org.example.console.ui.ConsoleUI;
import org.example.model.AuditLog;
import org.example.repository.AuditRepository;

import java.util.List;

public class DatabaseAuditRepository implements AuditRepository {
    private final ConsoleUI consoleUI;

    public DatabaseAuditRepository(ConsoleUI consoleUI) {
        this.consoleUI = consoleUI;
    }

    @Override
    public AuditLog save(AuditLog auditLog) {
        return null;
    }

    @Override
    public List<AuditLog> findByUsername(String username) {
        return List.of();
    }

    @Override
    public List<AuditLog> findAll() {
        return List.of();
    }
}

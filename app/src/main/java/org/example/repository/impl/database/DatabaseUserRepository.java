package org.example.repository.impl.database;

import org.example.console.ui.ConsoleUI;
import org.example.model.User;
import org.example.repository.UserRepository;

public class DatabaseUserRepository implements UserRepository {
    private final ConsoleUI consoleUI;

    public DatabaseUserRepository(ConsoleUI consoleUI) {
        this.consoleUI = consoleUI;
    }

    @Override
    public User findByUsername(String username) {
        return null;
    }

    @Override
    public User save(User user) {
        return null;
    }
}

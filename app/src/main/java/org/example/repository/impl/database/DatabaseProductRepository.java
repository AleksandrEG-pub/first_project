package org.example.repository.impl.database;

import org.example.console.ui.ConsoleUI;
import org.example.model.Product;
import org.example.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class DatabaseProductRepository implements ProductRepository {
    private final ConsoleUI consoleUI;

    public DatabaseProductRepository(ConsoleUI consoleUI) {
        this.consoleUI = consoleUI;
    }

    @Override
    public Product save(Product product) {
        return null;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Product> findAll() {
        return List.of();
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public List<Product> searchByName(String name) {
        return List.of();
    }

    @Override
    public List<Product> filterByCategory(String category) {
        return List.of();
    }

    @Override
    public List<Product> filterByBrand(String brand) {
        return List.of();
    }

    @Override
    public List<Product> filterByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return List.of();
    }
}

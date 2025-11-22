package org.example.repository.impl.file;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.example.console.ui.ConsoleUI;
import org.example.model.Product;
import org.example.repository.ProductRepository;

public class FileProductRepository extends BaseFileRepository implements ProductRepository {
  private static final AtomicLong counter = new AtomicLong(0);

  public FileProductRepository(ConsoleUI ui, File file) {
    super(ui, file);
  }

  public static void updateCounter(long value) {
    counter.set(value);
  }

  @Override
  public Optional<Product> findById(Long id) {
    return executeWithMetrics(
        () -> {
          if (id == null) return Optional.empty();

          List<String> lines = readAllLines();
          for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            Product product = parseCsvLine(line);
            if (id.equals(product.getId())) {
              return Optional.of(product);
            }
          }
          return Optional.empty();
        });
  }

  private Product parseCsvLine(String line) {
    String[] cols = CsvUtils.splitCsv(line);

    Product product = new Product();
    product.setId(cols.length > 0 ? getId(cols) : null);
    product.setName(cols.length > 1 ? cols[1] : null);
    product.setDescription(cols.length > 2 ? cols[2] : null);
    product.setCategory(cols.length > 3 ? cols[3] : null);
    product.setBrand(cols.length > 4 ? cols[4] : null);
    product.setPrice(parsePrice(cols.length > 5 ? cols[5] : null));

    return product;
  }

  private static Long getId(String[] cols) {
    return Long.parseLong(cols[0]);
  }

  private BigDecimal parsePrice(String priceStr) {
    if (priceStr == null || priceStr.trim().isEmpty()) {
      return null;
    }
    try {
      return new BigDecimal(priceStr.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }

  @Override
  public Product save(Product product) {
    if (product == null) {
      throw new IllegalArgumentException("Product cannot be null");
    }
    if (product.getId() == null) {
      Long id = counter.getAndIncrement();
      product.setId(id);
    }
    return executeWithMetrics(
        () -> {
          List<String> lines = readAllLines();
          List<String> newLines = new ArrayList<>();
          boolean updated = false;

          // Update existing or add new
          for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }

            Product existing = parseCsvLine(line);
            if (product.getId().equals(existing.getId())) {
              newLines.add(toCsvLine(product));
              updated = true;
            } else {
              newLines.add(line);
            }
          }

          if (!updated) {
            newLines.add(toCsvLine(product));
          }

          writeAllLines(newLines);
          return product;
        });
  }

  private String toCsvLine(Product product) {
    return String.join(
        ",",
        product.getId().toString(),
        CsvUtils.escapeCsv(product.getName()),
        CsvUtils.escapeCsv(product.getDescription()),
        CsvUtils.escapeCsv(product.getCategory()),
        CsvUtils.escapeCsv(product.getBrand()),
        product.getPrice() != null ? product.getPrice().toPlainString() : "");
  }

  @Override
  public boolean delete(Long id) {
    return executeWithMetrics(
        () -> {
          if (id == null) return false;

          List<String> lines = readAllLines();
          List<String> newLines = new ArrayList<>();
          boolean removed = false;

          for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            Product product = parseCsvLine(line);
            if (!id.equals(product.getId())) {
              newLines.add(line);
            } else {
              removed = true;
            }
          }

          if (removed) {
            writeAllLines(newLines);
          }
          return removed;
        });
  }

  @Override
  public List<Product> searchByName(String name) {
    return executeWithMetrics(
        () -> {
          if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
          }

          String searchTerm = name.toLowerCase().trim();
          return findAll().stream()
              .filter(
                  product ->
                      product.getName() != null
                          && product.getName().toLowerCase().contains(searchTerm))
              .toList();
        });
  }

  @Override
  public List<Product> findAll() {
    return executeWithMetrics(
        () -> {
          List<String> lines = readAllLines();
          return lines.stream()
              .filter(line -> !line.trim().isEmpty())
              .map(this::parseCsvLine)
              .filter(product -> product.getId() != null)
              .toList();
        });
  }

  @Override
  public List<Product> filterByCategory(String category) {
    return executeWithMetrics(
        () -> {
          if (category == null || category.trim().isEmpty()) {
            return new ArrayList<>();
          }

          String categoryFilter = category.trim();
          return findAll().stream()
              .filter(product -> categoryFilter.equals(product.getCategory()))
              .toList();
        });
  }

  @Override
  public List<Product> filterByBrand(String brand) {
    return executeWithMetrics(
        () -> {
          if (brand == null || brand.trim().isEmpty()) {
            return new ArrayList<>();
          }

          String brandFilter = brand.trim();
          return findAll().stream()
              .filter(product -> brandFilter.equals(product.getBrand()))
              .toList();
        });
  }

  @Override
  public List<Product> filterByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
    return executeWithMetrics(
        () ->
            findAll().stream()
                .filter(
                    product -> {
                      if (product.getPrice() == null) return false;
                      boolean matchesMin =
                          minPrice == null || product.getPrice().compareTo(minPrice) >= 0;
                      boolean matchesMax =
                          maxPrice == null || product.getPrice().compareTo(maxPrice) <= 0;
                      return matchesMin && matchesMax;
                    })
                .toList());
  }
}

package org.example.repository.impl.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.example.model.Product;
import org.example.repository.ProductRepository;
import org.example.repository.impl.database.mapper.ProductResultMapper;
import org.example_database.database.ConnectionManager;
import org.example_database.exception.DataAccessException;
import org.example_logging.annotation.WithTimingLog;
import org.springframework.stereotype.Component;

@Component
public class JdbcProductRepository implements ProductRepository {

  private static final String INSERT_SQL =
      """
        INSERT INTO products (name, description, category, brand, price)
        VALUES (?, ?, ?, ?, ?)
        """;

  private static final String UPDATE_SQL =
      """
        UPDATE products SET name = ?, description = ?, category = ?, brand = ?, price = ?
        WHERE id = ?
        """;

  private static final String FIND_BY_ID_SQL =
      """
        SELECT id, name, description, category, brand, price
        FROM products WHERE id = ?
        """;

  private static final String FIND_ALL_SQL =
      """
        SELECT id, name, description, category, brand, price
        FROM products
        """;

  private static final String DELETE_SQL = "DELETE FROM products WHERE id = ?";

  private static final String SEARCH_BY_NAME_SQL =
      """
        SELECT id, name, description, category, brand, price
        FROM products WHERE LOWER(name) LIKE LOWER(?)
        """;

  private static final String FILTER_BY_CATEGORY_SQL =
      """
        SELECT id, name, description, category, brand, price
        FROM products WHERE category = ?
        """;

  private static final String FILTER_BY_BRAND_SQL =
      """
        SELECT id, name, description, category, brand, price
        FROM products WHERE brand = ?
        """;

  private static final String FILTER_BY_PRICE_RANGE_SQL =
      """
        SELECT id, name, description, category, brand, price
        FROM products WHERE price BETWEEN ? AND ?
        """;

  private final ConnectionManager connectionManager;
  private final ProductResultMapper productResultMapper = new ProductResultMapper();

  public JdbcProductRepository(ConnectionManager connectionManager) {
    this.connectionManager = connectionManager;
  }

  @WithTimingLog
  @Override
  public Product save(Product product) {
    return connectionManager.doInTransaction(
        connection -> {
          if (product.getId() == null) {
            return insertProduct(connection, product);
          } else {
            return updateProduct(connection, product);
          }
        });
  }

  private Product insertProduct(Connection connection, Product product) {
    try (PreparedStatement stmt =
        connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, product.getName());
      stmt.setString(2, product.getDescription());
      stmt.setString(3, product.getCategory());
      stmt.setString(4, product.getBrand());
      stmt.setBigDecimal(5, product.getPrice());

      int affectedRows = stmt.executeUpdate();
      if (affectedRows == 0) {
        throw new DataAccessException("Creating product failed, no rows affected.");
      }

      try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          product.setId(generatedKeys.getLong(1));
        } else {
          throw new DataAccessException("Creating product failed, no ID obtained.");
        }
      }

      return product;
    } catch (SQLException e) {
      throw new DataAccessException("Failed to insert product", e);
    }
  }

  private Product updateProduct(Connection connection, Product product) {
    try (PreparedStatement stmt = connection.prepareStatement(UPDATE_SQL)) {
      stmt.setString(1, product.getName());
      stmt.setString(2, product.getDescription());
      stmt.setString(3, product.getCategory());
      stmt.setString(4, product.getBrand());
      stmt.setBigDecimal(5, product.getPrice());
      stmt.setLong(6, product.getId());

      int affectedRows = stmt.executeUpdate();
      if (affectedRows == 0) {
        throw new DataAccessException("Updating product failed, no rows affected.");
      }

      return product;
    } catch (SQLException e) {
      throw new DataAccessException("Failed to update product", e);
    }
  }

  @WithTimingLog
  @Override
  public Optional<Product> findById(Long id) {
    return connectionManager.doInTransaction(
        connection -> {
          try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_ID_SQL)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
              if (rs.next()) {
                return Optional.of(productResultMapper.mapToProduct(rs));
              } else {
                return Optional.empty();
              }
            }
          } catch (SQLException e) {
            throw new DataAccessException("Failed to find product by id: " + id, e);
          }
        });
  }

  @WithTimingLog
  @Override
  public List<Product> findAll() {
    return connectionManager.doInTransaction(
        connection -> {
          List<Product> products = new ArrayList<>();
          try (PreparedStatement stmt = connection.prepareStatement(FIND_ALL_SQL);
              ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
              products.add(productResultMapper.mapToProduct(rs));
            }
            return products;
          } catch (SQLException e) {
            throw new DataAccessException("Failed to find all products", e);
          }
        });
  }

  @WithTimingLog
  @Override
  public boolean delete(Long id) {
    return connectionManager.doInTransaction(
        connection -> {
          try (PreparedStatement stmt = connection.prepareStatement(DELETE_SQL)) {
            stmt.setLong(1, id);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
          } catch (SQLException e) {
            throw new DataAccessException("Failed to delete product with id: " + id, e);
          }
        });
  }

  @WithTimingLog
  @Override
  public List<Product> searchByName(String name) {
    return executeFilter(SEARCH_BY_NAME_SQL, "%" + name + "%");
  }

  private List<Product> executeFilter(String sql, String filterValue) {
    return connectionManager.doInTransaction(
        connection -> {
          List<Product> products = new ArrayList<>();
          try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, filterValue);

            try (ResultSet rs = stmt.executeQuery()) {
              while (rs.next()) {
                products.add(productResultMapper.mapToProduct(rs));
              }
            }
            return products;
          } catch (SQLException e) {
            throw new DataAccessException("Failed to execute filter", e);
          }
        });
  }

  @WithTimingLog
  @Override
  public List<Product> filterByCategory(String category) {
    return executeFilter(FILTER_BY_CATEGORY_SQL, category);
  }

  @WithTimingLog
  @Override
  public List<Product> filterByBrand(String brand) {
    return executeFilter(FILTER_BY_BRAND_SQL, brand);
  }

  @WithTimingLog
  @Override
  public List<Product> filterByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
    return connectionManager.doInTransaction(
        connection -> {
          List<Product> products = new ArrayList<>();
          try (PreparedStatement stmt = connection.prepareStatement(FILTER_BY_PRICE_RANGE_SQL)) {
            stmt.setBigDecimal(1, minPrice);
            stmt.setBigDecimal(2, maxPrice);

            try (ResultSet rs = stmt.executeQuery()) {
              while (rs.next()) {
                products.add(productResultMapper.mapToProduct(rs));
              }
            }
            return products;
          } catch (SQLException e) {
            throw new DataAccessException("Failed to filter products by price range", e);
          }
        });
  }
}

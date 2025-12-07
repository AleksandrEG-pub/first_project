package org.example.repository.impl.database.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.example.model.Product;

/** Jdbc result set mapping for products table to Product entity */
public class ProductResultMapper {
  public Product mapToProduct(ResultSet rs) throws SQLException {
    Product product = new Product();
    product.setId(rs.getLong("id"));
    product.setName(rs.getString("name"));
    product.setDescription(rs.getString("description"));
    product.setCategory(rs.getString("category"));
    product.setBrand(rs.getString("brand"));
    product.setPrice(rs.getBigDecimal("price"));
    return product;
  }
}

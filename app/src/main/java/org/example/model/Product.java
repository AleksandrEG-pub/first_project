package org.example.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {
  private Long id;
  private String name;
  private String description;
  private String category;
  private String brand;
  private BigDecimal price;

  public Product() {}

  public Product(
          Long id, String name, String description, String category, String brand, BigDecimal price) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.category = category;
    this.brand = brand;
    this.price = price;
  }

  public static ProductBuilder builder() {
    return new ProductBuilder();
  }

  public static ProductBuilder builder(Product product) {
    return new ProductBuilder()
        .id(product.getId())
        .name(product.getName())
        .description(product.getDescription())
        .brand(product.getBrand())
        .category(product.getCategory())
        .price(product.getPrice());
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getBrand() {
    return brand;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Product product = (Product) o;
    return Objects.equals(id, product.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return String.format(
        "Product{id='%s',name='%s',description='%s',category='%s',brand='%s',price=%s}",
        id, name, description, category, brand, price);
  }

  public static class ProductBuilder {
    private Long id;
    private String name;
    private String description;
    private String category;
    private String brand;
    private BigDecimal price;

    public ProductBuilder id(Long id) {
      this.id = id;
      return this;
    }

    public ProductBuilder name(String name) {
      this.name = name;
      return this;
    }

    public ProductBuilder description(String description) {
      this.description = description;
      return this;
    }

    public ProductBuilder category(String category) {
      this.category = category;
      return this;
    }

    public ProductBuilder brand(String brand) {
      this.brand = brand;
      return this;
    }

    public ProductBuilder price(BigDecimal price) {
      this.price = price;
      return this;
    }

    public Product build() {
      return new Product(id, name, description, category, brand, price);
    }
  }
}

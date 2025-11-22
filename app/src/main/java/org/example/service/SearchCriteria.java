package org.example.service;

import java.math.BigDecimal;
import org.example.model.Product;

public class SearchCriteria {
  private final Long id;
  private final String name;
  private final String category;
  private final String brand;
  private final BigDecimal minPrice;
  private final BigDecimal maxPrice;

  private SearchCriteria(Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.category = builder.category;
    this.brand = builder.brand;
    this.minPrice = builder.minPrice;
    this.maxPrice = builder.maxPrice;
  }

  public boolean matchesId(Product product) {
    return product.getId() != null && product.getId().equals(this.id);
  }

  public boolean matchesName(Product product) {
    if (this.name == null || this.name.trim().isEmpty()) {
      return true;
    }
    return product.getName() != null
        && product.getName().toLowerCase().contains(this.name.toLowerCase().trim());
  }

  public boolean matchesCategory(Product product) {
    if (this.category == null || this.category.trim().isEmpty()) {
      return true;
    }
    return product.getCategory() != null
        && product.getCategory().equalsIgnoreCase(this.category.trim());
  }

  public boolean matchesBrand(Product product) {
    if (this.brand == null || this.brand.trim().isEmpty()) {
      return true;
    }
    return product.getBrand() != null && product.getBrand().equalsIgnoreCase(this.brand.trim());
  }

  public boolean matchesPriceRange(Product product) {
    if (product.getPrice() == null) {
      return false;
    }

    boolean matchesMin = this.minPrice == null || product.getPrice().compareTo(this.minPrice) >= 0;
    boolean matchesMax = this.maxPrice == null || product.getPrice().compareTo(this.maxPrice) <= 0;

    return matchesMin && matchesMax;
  }

  public String getName() {
    return name;
  }

  public String getCategory() {
    return category;
  }

  public String getBrand() {
    return brand;
  }

  public BigDecimal getMinPrice() {
    return minPrice;
  }

  public BigDecimal getMaxPrice() {
    return maxPrice;
  }

  public boolean isEmpty() {
    return id == null
        && name == null
        && category == null
        && brand == null
        && minPrice == null
        && maxPrice == null;
  }

  public static class Builder {
    private Long id;
    private String name;
    private String category;
    private String brand;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder category(String category) {
      this.category = category;
      return this;
    }

    public Builder brand(String brand) {
      this.brand = brand;
      return this;
    }

    public Builder minPrice(BigDecimal minPrice) {
      this.minPrice = minPrice;
      return this;
    }

    public Builder maxPrice(BigDecimal maxPrice) {
      this.maxPrice = maxPrice;
      return this;
    }

    public SearchCriteria build() {
      return new SearchCriteria(this);
    }
  }
}

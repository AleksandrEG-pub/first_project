package org.example.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.Product;

/**
 * Parameter for product search. Minimal sensible validation, so search can be as flexible as
 * possible, excluding most unrealistic values, like negative price.
 */
@EqualsAndHashCode
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {
  private Long id;
  private String name;
  private String category;
  private String brand;
  private BigDecimal minPrice;
  private BigDecimal maxPrice;

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

  public boolean isEmpty() {
    return id == null
        && name == null
        && category == null
        && brand == null
        && minPrice == null
        && maxPrice == null;
  }
}

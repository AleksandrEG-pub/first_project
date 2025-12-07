package org.example.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class Product {

  private Long id;

  @NotNull(message = "name can not be null")
  @NotBlank(message = "name can not be empty")
  @Length(max = 255, message = "name can not be longer 255")
  private String name;

  @NotNull(message = "description can not be null")
  @NotBlank(message = "description can not be empty")
  @Length(max = 10000, message = "description can not be longer 10000")
  private String description;

  @NotNull(message = "category can not be null")
  @NotBlank(message = "category can not be empty")
  @Length(max = 255, message = "category can not be longer 255")
  private String category;

  @NotNull(message = "brand can not be null")
  @NotBlank(message = "brand can not be empty")
  @Length(max = 255, message = "brand can not be longer 255")
  private String brand;

  @NotNull(message = "price can not be null")
  @DecimalMin(value = "0.01", message = "minimal price is 0.01")
  private BigDecimal price;

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
}

package org.example.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Response entity for user for of the application */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductDto {
  private Long id;
  private String name;
  private String description;
  private String category;
  private String brand;
  private BigDecimal price;
}

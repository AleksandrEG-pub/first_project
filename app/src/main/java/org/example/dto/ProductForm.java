package org.example.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.Product;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@Getter
@Setter
public class ProductForm {
  @NotNull
  @NotBlank
  @Length(max = 255)
  private String name;

  @NotNull
  @NotBlank
  @Length(max = 10000)
  private String description;

  @NotNull
  @NotBlank
  @Length(max = 255)
  private String category;

  @NotNull
  @NotBlank
  @Length(max = 255)
  private String brand;

  @NotNull
  @DecimalMin(value = "0.01")
  private BigDecimal price;

  public static ProductForm fromProduct(Product product) {
    ProductForm productForm = new ProductForm();
    productForm.setName(product.getName());
    productForm.setDescription(product.getDescription());
    productForm.setCategory(product.getCategory());
    productForm.setBrand(product.getBrand());
    productForm.setPrice(product.getPrice());
    return productForm;
  }
}

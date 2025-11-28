package org.example.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.Product;
import org.hibernate.validator.constraints.Length;

/** Data received from user, which will be used as a blueprint to create new product */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductForm {
  @NotNull(message = "name can not be null")
  @NotBlank(message = "name can not be empty")
  @Length(max = 255, message = "name can not be longer than 255")
  private String name;

  @NotNull(message = "description can not be null")
  @NotBlank(message = "description can not be empty")
  @Length(max = 10000, message = "description can not be longer than 10000")
  private String description;

  @NotNull(message = "category can not be null")
  @NotBlank(message = "category can not be empty")
  @Length(max = 255, message = "category can not be longer than 255")
  private String category;

  @NotNull(message = "brand can not be null")
  @NotBlank(message = "brand can not be empty")
  @Length(max = 255, message = "brand can not be longer than 255")
  private String brand;

  @NotNull(message = "price can not be null")
  @DecimalMin(value = "0.01", message = "minimal price is 0,01")
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

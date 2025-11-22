package org.example.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.Product;

@NoArgsConstructor
@Getter
@Setter
public class ProductForm {
  private String name;
  private String description;
  private String category;
  private String brand;
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

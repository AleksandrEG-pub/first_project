package org.example.mapper;

import org.example.dto.ProductDto;
import org.example.dto.ProductForm;
import org.example.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", config = DefaultMappingConfig.class)
public interface ProductMapper {
  @Mapping(target = "id", ignore = true)
  Product toProduct(ProductForm productForm);

  ProductDto toDto(Product product);
}

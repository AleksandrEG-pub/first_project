package org.example.mapper;

import org.example.dto.ProductForm;
import org.example.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {
  ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

  @Mapping(target = "id", ignore = true)
  Product toProduct(ProductForm productForm);
}

package org.example.web.controller;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.ProductDto;
import org.example.dto.ProductForm;
import org.example.dto.SearchCriteria;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.ProductMapper;
import org.example.model.Product;
import org.example.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequestMapping(value = "/products", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class ProductController {
  private final ProductMapper productMapper;
  private final ProductService productService;

  @GetMapping(value = "/{id}")
  public ResponseEntity<ProductDto> getById(@PathVariable Long id) {
    return productService
        .findById(id)
        .map(productMapper::toDto)
        .map(
            productDto ->
                ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(productDto))
        .orElseThrow(() -> new ResourceNotFoundException("product", String.valueOf(id)));
  }

  @GetMapping
  public ResponseEntity<List<ProductDto>> getFilter(
      @RequestParam(required = false) Long id,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String brand,
      @RequestParam(required = false) BigDecimal minPrice,
      @RequestParam(required = false) BigDecimal maxPrice) {
    SearchCriteria criteria =
        SearchCriteria.builder()
            .id(id)
            .name(name)
            .category(category)
            .brand(brand)
            .minPrice(minPrice)
            .maxPrice(maxPrice)
            .build();
    var response = ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON);
    if (criteria.isEmpty()) {
      List<ProductDto> products =
          productService.getAllProducts().stream().map(productMapper::toDto).toList();
      return response.body(products);
    } else {
      List<ProductDto> products =
          productService.search(criteria).stream().map(productMapper::toDto).toList();
      return response.body(products);
    }
  }

  @PostMapping
  public ResponseEntity<ProductDto> create(@RequestBody @Valid ProductForm productForm) {
    Product product = productService.create(productForm);
    return ResponseEntity.status(HttpStatus.CREATED)
        .contentType(MediaType.APPLICATION_JSON)
        .body(productMapper.toDto(product));
  }

  @PutMapping(value = "/{id}")
  public ResponseEntity<ProductDto> update(
      @PathVariable long id, @RequestBody @Valid ProductForm productForm) {
    Product product = productService.updateProduct(id, productForm);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(productMapper.toDto(product));
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable long id) {
    productService.deleteProduct(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}

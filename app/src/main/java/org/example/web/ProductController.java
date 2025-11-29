package org.example.web;

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
import org.springframework.http.MediaType;
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
  private static final ProductMapper PRODUCT_MAPPER = ProductMapper.INSTANCE;
  private final transient ProductService productService;

  @GetMapping(value = "/{id}")
  public ProductDto getById(@PathVariable Long id) {
    return productService
        .findById(id)
        .map(PRODUCT_MAPPER::toDto)
        .orElseThrow(() -> new ResourceNotFoundException("product", String.valueOf(id)));
  }

  @GetMapping
  public List<ProductDto> getFilter(
      @RequestParam(required = false) Long id,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String brand,
      @RequestParam(required = false) BigDecimal minPrice,
      @RequestParam(required = false) BigDecimal maxPrice) {
    SearchCriteria criteria = new SearchCriteria.Builder().build();
    if (criteria == null) {
      return productService.getAllProducts().stream().map(PRODUCT_MAPPER::toDto).toList();
    } else {
      return productService.search(criteria).stream().map(PRODUCT_MAPPER::toDto).toList();
    }
  }

  @PostMapping
  public ProductDto create(@RequestBody @Valid ProductForm productForm) {
    Product product = productService.create(productForm);
    return PRODUCT_MAPPER.toDto(product);
  }

  @PutMapping(value = "/{id}")
  public ProductDto update(@PathVariable long id, @RequestBody @Valid ProductForm productForm) {
    Product product = productService.updateProduct(id, productForm);
    return PRODUCT_MAPPER.toDto(product);
  }

  @DeleteMapping(value = "/{id}")
  public void delete(@PathVariable long id) {
    productService.deleteProduct(id);
  }
}

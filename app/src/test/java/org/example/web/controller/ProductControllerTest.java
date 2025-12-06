package org.example.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.example.configuration.BeanConfiguration;
import org.example.dto.ProductDto;
import org.example.dto.ProductForm;
import org.example.dto.SearchCriteria;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.ProductMapper;
import org.example.model.Product;
import org.example.service.ProductService;
import org.example_web_common.web.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringJUnitConfig(classes = {BeanConfiguration.class})
@TestPropertySource(locations = "classpath:application.properties")
class ProductControllerTest {

  MockMvc mockMvc;

  @MockBean
  ProductService productService;
  @Autowired ObjectMapper objectMapper;
  ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

  @BeforeEach
  void setup() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(new ProductController(productMapper, productService))
            .setControllerAdvice(AppExceptionHandler.class, GlobalExceptionHandler.class)
            .build();
  }

  private Product createProduct(Long id, String name) {
    Product product = new Product();
    product.setId(id);
    product.setName(name);
    product.setDescription("Test Description");
    product.setCategory("Test Category");
    product.setBrand("Test Brand");
    product.setPrice(BigDecimal.valueOf(99.99));
    return product;
  }

  private ProductForm createProductForm(String name) {
    return new ProductForm(
        name, "Test Description", "Test Category", "Test Brand", BigDecimal.valueOf(99.99));
  }

  @Test
  void getByInvalidMethod_ShouldReturnMethodNotAllowed() throws Exception {
    mockMvc.perform(patch("/products")).andExpect(status().isMethodNotAllowed());
  }

  @Nested
  class GetByIdTests {

    @Test
    void getById_WhenValidId_ShouldReturnProduct() throws Exception {
      // Arrange
      Long productId = 1L;
      Product product = createProduct(productId, "Test Product");
      ProductDto expectedDto = productMapper.toDto(product);

      when(productService.findById(productId)).thenReturn(Optional.of(product));

      // Act & Assert
      mockMvc
          .perform(get("/products/{id}", productId))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().json(objectMapper.writeValueAsString(expectedDto)));

      verify(productService).findById(productId);
    }

    @Test
    void getById_WhenProductNotFound_ShouldReturnNotFound() throws Exception {
      // Arrange
      Long productId = 999L;
      when(productService.findById(productId)).thenReturn(Optional.empty());

      // Act & Assert
      mockMvc
          .perform(get("/products/{id}", productId))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.title").value("resource_not_found"))
          .andExpect(jsonPath("$.detail").value("Resource [product] not found by id: [999]"));

      verify(productService).findById(productId);
    }
  }

  @Nested
  class GetFilterTests {

    @Test
    void getFilter_WhenNoParameters_ShouldReturnAllProducts() throws Exception {
      // Arrange
      List<Product> products =
          List.of(createProduct(1L, "Product1"), createProduct(2L, "Product2"));
      List<ProductDto> expectedDtos = products.stream().map(productMapper::toDto).toList();

      when(productService.getAllProducts()).thenReturn(products);

      // Act & Assert
      mockMvc
          .perform(get("/products"))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().json(objectMapper.writeValueAsString(expectedDtos)));

      verify(productService).getAllProducts();
      verify(productService, never()).search(any(SearchCriteria.class));
    }

    @Test
    void getFilter_WhenNameParameterPresent_ShouldHandleSearch() throws Exception {
      // Arrange
      List<Product> products =
          List.of(createProduct(1L, "Test Product"), createProduct(2L, "Another Test Product"));
      List<ProductDto> expectedDtos = products.stream().map(productMapper::toDto).toList();

      when(productService.search(any(SearchCriteria.class))).thenReturn(products);

      // Act & Assert
      mockMvc
          .perform(get("/products").param("name", "test"))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().json(objectMapper.writeValueAsString(expectedDtos)));

      verify(productService).search(any(SearchCriteria.class));
      verify(productService, never()).getAllProducts();
    }

    @Test
    void getFilter_WhenCategoryParameterPresent_ShouldHandleSearch() throws Exception {
      // Arrange
      List<Product> products = List.of(createProduct(1L, "Product1"));
      List<ProductDto> expectedDtos = products.stream().map(productMapper::toDto).toList();

      when(productService.search(any(SearchCriteria.class))).thenReturn(products);

      // Act & Assert
      mockMvc
          .perform(get("/products").param("category", "electronics"))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().json(objectMapper.writeValueAsString(expectedDtos)));

      verify(productService).search(any(SearchCriteria.class));
    }

    @Test
    void getFilter_WhenMultipleSearchParametersPresent_ShouldHandleSearch() throws Exception {
      // Arrange
      List<Product> products = List.of(createProduct(1L, "Product1"));
      List<ProductDto> expectedDtos = products.stream().map(productMapper::toDto).toList();

      when(productService.search(any(SearchCriteria.class))).thenReturn(products);

      // Act & Assert
      mockMvc
          .perform(
              get("/products")
                  .param("name", "test")
                  .param("category", "electronics")
                  .param("brand", "sony")
                  .param("minPrice", "100")
                  .param("maxPrice", "500"))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().json(objectMapper.writeValueAsString(expectedDtos)));

      verify(productService).search(any(SearchCriteria.class));
      verify(productService, never()).getAllProducts();
    }

    @Test
    void getFilter_WhenNoProductsFound_ShouldReturnEmptyArray() throws Exception {
      // Arrange
      when(productService.getAllProducts()).thenReturn(List.of());

      // Act & Assert
      mockMvc
          .perform(get("/products"))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().json("[]"));

      verify(productService).getAllProducts();
    }
  }

  @Nested
  class PostTests {

    @Test
    void create_WhenValidRequest_ShouldCreateProduct() throws Exception {
      // Arrange
      ProductForm productForm = createProductForm("New Product");
      Product createdProduct = createProduct(1L, "New Product");
      ProductDto expectedDto = productMapper.toDto(createdProduct);

      // Act
      when(productService.create(productForm)).thenReturn(createdProduct);

      // Assert
      mockMvc
          .perform(
              post("/products")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(productForm)))
          .andExpect(status().isCreated())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().json(objectMapper.writeValueAsString(expectedDto)));

      verify(productService).create(productForm);
    }

    @Test
    void create_WhenInvalidRequest_ShouldReturnBadRequest() throws Exception {
      // Arrange
      ProductForm invalidForm = createProductForm(""); // Empty name should trigger validation

      // Act & Assert
      mockMvc
          .perform(
              post("/products")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(invalidForm)))
          .andExpect(status().isBadRequest());

      verify(productService, never()).create(any(ProductForm.class));
    }
  }

  @Nested
  class PutTests {

    @Test
    void update_WhenValidRequest_ShouldUpdateProduct() throws Exception {
      // Arrange
      Long productId = 1L;
      ProductForm updateForm = createProductForm("Updated Product");
      Product updatedProduct = createProduct(productId, "Updated Product");
      ProductDto expectedDto = productMapper.toDto(updatedProduct);

      when(productService.updateProduct(productId, updateForm)).thenReturn(updatedProduct);

      // Act & Assert
      mockMvc
          .perform(
              put("/products/{id}", productId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateForm)))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().json(objectMapper.writeValueAsString(expectedDto)));

      verify(productService).updateProduct(productId, updateForm);
    }

    @Test
    void update_WhenInvalidId_ShouldReturnNotFound() throws Exception {
      // Arrange
      Long productId = 999L;
      ProductForm updateForm = createProductForm("Updated Product");

      when(productService.updateProduct(productId, updateForm))
          .thenThrow(new ResourceNotFoundException("product", String.valueOf(productId)));

      // Act & Assert
      mockMvc
          .perform(
              put("/products/{id}", productId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateForm)))
          .andExpect(status().isNotFound());

      verify(productService).updateProduct(productId, updateForm);
    }
  }

  @Nested
  class DeleteTests {

    @Test
    void delete_WhenValidId_ShouldDeleteProduct() throws Exception {
      // Arrange
      Long productId = 1L;

      // Act & Assert
      mockMvc.perform(delete("/products/{id}", productId)).andExpect(status().isNoContent());

      verify(productService).deleteProduct(productId);
    }

    @Test
    void delete_WhenInvalidId_ShouldReturnNotFound() throws Exception {
      // Arrange
      Long productId = 999L;
      doThrow(new ResourceNotFoundException("product", String.valueOf(productId)))
          .when(productService)
          .deleteProduct(productId);

      // Act & Assert
      mockMvc.perform(delete("/products/{id}", productId)).andExpect(status().isNotFound());

      verify(productService).deleteProduct(productId);
    }
  }
}

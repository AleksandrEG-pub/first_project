package org.example.web.servlet;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.example.dto.ProductDto;
import org.example.dto.ProductForm;
import org.example.dto.SearchCriteria;
import org.example.exception.MissingRequestParameterException;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Product;
import org.example.service.CriteriaRequestParser;
import org.example.service.DtoValidator;
import org.example.service.ProductFormRequestParser;
import org.example.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ProductServletTest {

  private ObjectMapper objectMapper;
  private ProductServlet productServlet;
  private StringWriter responseWriter;
  private ProductService productService;
  private CriteriaRequestParser criteriaRequestParser;
  private ProductFormRequestParser productFormRequestParser;
  private DtoValidator dtoValidator;
  private HttpServletRequest request;
  private HttpServletResponse response;

  @BeforeEach
  void setUp() throws IOException {
    productService = Mockito.mock(ProductService.class);
    criteriaRequestParser = Mockito.mock(CriteriaRequestParser.class);
    productFormRequestParser = Mockito.mock(ProductFormRequestParser.class);
    dtoValidator = Mockito.mock(DtoValidator.class);
    request = Mockito.mock(HttpServletRequest.class);
    response = Mockito.mock(HttpServletResponse.class);
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    productServlet =
        new ProductServlet(
            productService,
            criteriaRequestParser,
            productFormRequestParser,
            dtoValidator,
            objectMapper);
    responseWriter = new StringWriter();
    when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
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

  @Nested
  class GetByIdTest {
    @Test
    void doGet_WhenPathHasId_ShouldReturnProductById() throws IOException {
      // Arrange
      Long productId = 1L;
      Product product = createProduct(productId, "Test Product");

      when(request.getPathInfo()).thenReturn("/" + productId);
      when(productService.findById(productId)).thenReturn(Optional.of(product));

      // Act
      productServlet.doGet(request, response);

      // Assert
      verify(productService).findById(productId);
      String jsonResponse = responseWriter.toString();
      ProductDto responseDto = objectMapper.readValue(jsonResponse, ProductDto.class);

      assertThat(responseDto.getId()).isEqualTo(productId);
      verify(response).setStatus(HttpServletResponse.SC_OK);
      verify(response).setContentType("application/json");
    }

    @Test
    void doGet_WhenValidId_ShouldExtractIdSuccessfully() {
      // Arrange
      Long productId = 123L;
      Product product = createProduct(productId, "Test Product");

      when(request.getPathInfo()).thenReturn("/" + productId);
      when(productService.findById(productId)).thenReturn(Optional.of(product));

      // Act & Assert - No exception means ID extraction worked
      assertThatNoException().isThrownBy(() -> productServlet.doGet(request, response));
      verify(productService).findById(123L);
    }

    @Test
    void doGet_WhenPathHasIdAndProductNotFound_ShouldThrowResourceNotFoundException() {
      // Arrange
      Long productId = 999L;
      when(request.getPathInfo()).thenReturn("/" + productId);
      when(productService.findById(productId)).thenReturn(Optional.empty());

      // Act & Assert
      var asserted =
          assertThatThrownBy(() -> productServlet.doGet(request, response))
              .isInstanceOf(ResourceNotFoundException.class);
      asserted
          .extracting(e -> (ResourceNotFoundException) e)
          .extracting(ResourceNotFoundException::getResource)
          .isEqualTo("product");
      asserted
          .extracting(e -> (ResourceNotFoundException) e)
          .extracting(ResourceNotFoundException::getId)
          .isEqualTo(String.valueOf(productId));
    }
  }

  @Nested
  class GetSearchTests {

    @Test
    void doGet_WhenNameParameterPresent_ShouldHandleSearch() throws IOException {
      // Arrange
      SearchCriteria criteria = new SearchCriteria.Builder().build();
      List<Product> products =
          List.of(createProduct(1L, "Product1"), createProduct(2L, "Product2"));

      when(request.getPathInfo()).thenReturn(null);
      when(request.getParameter("name")).thenReturn("test");
      when(criteriaRequestParser.buildSearchCriteria(request)).thenReturn(criteria);
      when(productService.search(criteria)).thenReturn(products);

      // Act
      productServlet.doGet(request, response);

      // Assert
      verify(criteriaRequestParser).buildSearchCriteria(request);
      verify(productService).search(criteria);
      verify(productService, never()).getAllProducts();

      String jsonResponse = responseWriter.toString();
      List<?> responseList = objectMapper.readValue(jsonResponse, List.class);
      assertThat(responseList).hasSize(2);
      verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doGet_WhenCategoryParameterPresent_ShouldHandleSearch() {
      // Arrange
      SearchCriteria criteria = new SearchCriteria.Builder().build();
      List<Product> products = List.of(createProduct(1L, "Product1"));

      when(request.getPathInfo()).thenReturn(null);
      when(request.getParameter("category")).thenReturn("electronics");
      when(criteriaRequestParser.buildSearchCriteria(request)).thenReturn(criteria);
      when(productService.search(criteria)).thenReturn(products);

      // Act
      productServlet.doGet(request, response);

      // Assert
      verify(productService).search(criteria);
      verify(productService, never()).getAllProducts();
    }

    @Test
    void doGet_WhenMultipleSearchParametersPresent_ShouldHandleSearch() {
      // Arrange
      SearchCriteria criteria = new SearchCriteria.Builder().build();
      List<Product> products = List.of(createProduct(1L, "Product1"));

      when(request.getPathInfo()).thenReturn(null);
      when(request.getParameter("name")).thenReturn("test");
      when(request.getParameter("category")).thenReturn("electronics");
      when(request.getParameter("brand")).thenReturn("sony");
      when(request.getParameter("minPrice")).thenReturn("100");
      when(request.getParameter("maxPrice")).thenReturn("500");
      when(criteriaRequestParser.buildSearchCriteria(request)).thenReturn(criteria);
      when(productService.search(criteria)).thenReturn(products);

      // Act
      productServlet.doGet(request, response);

      // Assert
      verify(productService).search(criteria);
      verify(productService, never()).getAllProducts();
    }

    @Test
    void doGet_WhenNoPathInfoAndNoSearchParams_ShouldReturnAllProducts() throws IOException {
      // Arrange
      List<Product> products =
          List.of(createProduct(1L, "Product1"), createProduct(2L, "Product2"));

      when(request.getPathInfo()).thenReturn(null);
      when(request.getParameter(anyString())).thenReturn(null);
      when(productService.getAllProducts()).thenReturn(products);

      // Act
      productServlet.doGet(request, response);

      // Assert
      verify(productService).getAllProducts();
      verify(productService, never()).search(any());
      verify(productService, never()).findById(any());

      String jsonResponse = responseWriter.toString();
      List<?> responseList = objectMapper.readValue(jsonResponse, List.class);
      assertThat(responseList).hasSize(2);
      verify(response).setStatus(HttpServletResponse.SC_OK);
    }
  }

  @Nested
  class PostTests {

    @Test
    void doPost_WhenValidRequest_ShouldCreateProduct() throws IOException {
      // Arrange
      ProductForm productForm =
          new ProductForm(
              "New Product", "description", "Category", "Brand", BigDecimal.valueOf(100.0));
      Product createdProduct = createProduct(1L, "New Product");

      when(productFormRequestParser.parse(request)).thenReturn(productForm);
      when(productService.create(productForm)).thenReturn(createdProduct);

      // Act
      productServlet.doPost(request, response);

      // Assert
      verify(productFormRequestParser).parse(request);
      verify(dtoValidator).validate(productForm);
      verify(productService).create(productForm);

      String jsonResponse = responseWriter.toString();
      ProductDto responseDto = objectMapper.readValue(jsonResponse, ProductDto.class);
      assertThat(responseDto.getId()).isEqualTo(1L);
      verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    void doPost_WhenValidationFails_ShouldThrowException() {
      // Arrange
      ProductForm productForm = new ProductForm("", "", "", "", BigDecimal.valueOf(-10.0));
      when(productFormRequestParser.parse(request)).thenReturn(productForm);
      doThrow(new ValidationException("Validation failed"))
          .when(dtoValidator)
          .validate(productForm);

      // Act & Assert
      assertThatThrownBy(() -> productServlet.doPost(request, response))
          .isInstanceOf(ValidationException.class)
          .hasMessage("Validation failed");
    }
  }

  @Nested
  class PutTests {

    @Test
    void doPut_WhenValidRequest_ShouldUpdateProduct() throws IOException {
      // Arrange
      Long productId = 1L;
      ProductForm updateForm =
          new ProductForm(
              "Updated Product", "DESCRIPTION", "Category", "Brand", BigDecimal.valueOf(150.0));
      Product updatedProduct = createProduct(productId, "Updated Product");

      when(request.getPathInfo()).thenReturn("/" + productId);
      when(productFormRequestParser.parse(request)).thenReturn(updateForm);
      when(productService.updateProduct(productId, updateForm)).thenReturn(updatedProduct);

      // Act
      productServlet.doPut(request, response);

      // Assert
      verify(productFormRequestParser).parse(request);
      verify(dtoValidator).validate(updateForm);
      verify(productService).updateProduct(productId, updateForm);

      String jsonResponse = responseWriter.toString();
      ProductDto responseDto = objectMapper.readValue(jsonResponse, ProductDto.class);
      assertThat(responseDto.getId()).isEqualTo(productId);
      verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doPut_WhenInvalidIdFormat_ShouldThrowParameterTypeMismatchException() {
      // Arrange
      when(request.getPathInfo()).thenReturn("/abc");
      when(productFormRequestParser.parse(request))
          .thenReturn(new ProductForm("Name", "Desc", "Cat", "Brand", BigDecimal.TEN));

      // Act & Assert
      assertThatThrownBy(() -> productServlet.doPut(request, response))
          .isInstanceOf(MissingRequestParameterException.class);
    }
  }

  @Nested
  class DeleteTest {

    @Test
    void doDelete_WhenValidId_ShouldDeleteProduct() throws IOException {
      // Arrange
      Long productId = 1L;
      when(request.getPathInfo()).thenReturn("/" + productId);

      // Act
      productServlet.doDelete(request, response);

      // Assert
      verify(productService).deleteProduct(productId);
      verify(response).setStatus(HttpServletResponse.SC_OK);
      verify(response, never()).getWriter();
    }

    @Test
    void doDelete_WhenNegativeId_ShouldThrowValidationException() {
      // Arrange
      when(request.getPathInfo()).thenReturn("/-1");

      // Act & Assert
      assertThatThrownBy(() -> productServlet.doDelete(request, response))
          .isInstanceOf(MissingRequestParameterException.class);
    }
  }
}

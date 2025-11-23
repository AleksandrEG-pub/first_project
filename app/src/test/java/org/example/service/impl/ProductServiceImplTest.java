package org.example.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.validation.ValidationException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.example.cache.Cache;
import org.example.dto.ProductForm;
import org.example.dto.SearchCriteria;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Product;
import org.example.repository.ProductRepository;
import org.example.service.AuthService;
import org.example.service.DtoValidator;
import org.example.service.ProductSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ProductServiceImplTest {

  @Mock private ProductRepository productRepository;
  @Mock private Cache<Long, Product> productCache;
  @Mock private AuthService authService;
  @Mock private DtoValidator dtoValidator;
  @Mock private ProductSearchService productSearchService;

  private ProductServiceImpl productService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    productService =
        new ProductServiceImpl(
            productRepository, productCache, authService, dtoValidator, productSearchService);
  }

  @Test
  void search_ShouldDelegateToProductSearchService() {
    // Given
    SearchCriteria criteria =
        new SearchCriteria.Builder()
            .name("laptop")
            .category("Electronics")
            .category("Dell")
            .minPrice(null)
            .maxPrice(null)
            .build();
    List<Product> expectedProducts = List.of(createTestProduct(1L), createTestProduct(2L));
    when(productSearchService.search(criteria)).thenReturn(expectedProducts);

    // When
    List<Product> result = productService.search(criteria);

    // Then
    assertThat(result).isEqualTo(expectedProducts);
    verify(productSearchService).search(criteria);
  }

  private Product createTestProduct(Long id) {
    return Product.builder()
        .id(id)
        .name("Test Product")
        .description("Test Description")
        .category("Electronics")
        .brand("Test Brand")
        .price(new BigDecimal("99.99"))
        .build();
  }

  @Test
  void getAllProducts_ShouldDelegateToProductSearchService() {
    // Given
    List<Product> expectedProducts = List.of(createTestProduct(1L), createTestProduct(2L));
    when(productSearchService.getAllProducts()).thenReturn(expectedProducts);

    // When
    List<Product> result = productService.getAllProducts();

    // Then
    assertThat(result).isEqualTo(expectedProducts);
    verify(productSearchService).getAllProducts();
  }

  @Test
  void addProduct_ShouldAddProductSuccessfully() {
    // Given
    Product product = createTestProduct(null);
    Product savedProduct = createTestProduct(1L);
    String adminUsername = "admin";

    doNothing().when(authService).requireAdmin();
    doNothing().when(dtoValidator).validate(product);
    when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
    when(authService.getAdminUserName()).thenReturn(adminUsername);

    // When
    Product result = productService.addProduct(product);

    // Then
    assertThat(result).isEqualTo(savedProduct);

    verify(authService).requireAdmin();
    verify(dtoValidator).validate(product);
    verify(productRepository).save(any(Product.class));
    verify(productCache).put(1L, savedProduct);
  }

  @Test
  void addProduct_ShouldThrowException_WhenNotAdmin() {
    // Given
    Product product = createTestProduct(null);
    doThrow(new SecurityException("Admin access required")).when(authService).requireAdmin();

    // When & Then
    assertThatThrownBy(() -> productService.addProduct(product))
        .isInstanceOf(SecurityException.class)
        .hasMessage("Admin access required");

    verify(dtoValidator, never()).validate(any());
    verify(productRepository, never()).save(any());
  }

  @Test
  void addProduct_ShouldThrowException_WhenValidationFails() {
    // Given
    Product product = createTestProduct(null);
    doNothing().when(authService).requireAdmin();
    doThrow(new IllegalArgumentException("Invalid product data"))
        .when(dtoValidator)
        .validate(product);

    // When & Then
    assertThatThrownBy(() -> productService.addProduct(product))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Invalid product data");

    verify(productRepository, never()).save(any());
    verify(productCache, never()).put(anyLong(), any());
  }

  @Test
  void deleteProduct_ShouldReturnFalse_WhenIdIsNull() {
    // Given
    doNothing().when(authService).requireAdmin();

    // When
    Assertions.assertThatThrownBy(() -> productService.deleteProduct(null))
        .isInstanceOf(ValidationException.class);

    // Then
    verify(productRepository, never()).findById(any());
    verify(productRepository, never()).delete(any());
  }

  @Test
  void deleteProduct_ShouldReturnFalse_WhenProductNotFound() {
    // Given
    Long productId = 1L;
    doNothing().when(authService).requireAdmin();
    when(productRepository.findById(productId)).thenReturn(Optional.empty());

    // When
    Assertions.assertThatThrownBy(() -> productService.deleteProduct(productId))
        .isInstanceOf(ResourceNotFoundException.class);

    // Then
    verify(productRepository).findById(productId);
    verify(productRepository, never()).delete(productId);
    verify(productCache, never()).remove(any());
  }

  @Test
  void deleteProduct_ShouldDeleteSuccessfully_WhenProductExists() {
    // Given
    Long productId = 1L;
    Product product = createTestProduct(productId);

    doNothing().when(authService).requireAdmin();
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(productRepository.delete(productId)).thenReturn(true);

    // When
    productService.deleteProduct(productId);

    // Then
    verify(productRepository).findById(productId);
    verify(productRepository).delete(productId);
    verify(productCache).remove(productId);
  }

  @Test
  void deleteProduct_ShouldReturnFalse_WhenDeleteFails() {
    // Given
    Long productId = 1L;
    Product product = createTestProduct(productId);

    doNothing().when(authService).requireAdmin();
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(productRepository.delete(productId)).thenReturn(false);

    // When
    Assertions.assertThatThrownBy(() -> productService.deleteProduct(productId))
        .isInstanceOf(ResourceNotFoundException.class);
    // Then
    verify(productCache, never()).remove(productId);
  }

  @Test
  void updateProduct_ShouldUpdateSuccessfully() {
    // Given
    Long productId = 1L;
    Product existingProduct = createTestProduct(productId);
    Product newProductData = createUpdatedTestProduct();
    Product updatedProduct = createUpdatedTestProduct(productId);

    doNothing().when(authService).requireAdmin();
    doNothing().when(dtoValidator).validate(newProductData);
    when(productSearchService.findById(productId)).thenReturn(Optional.of(existingProduct));
    when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

    // When
    Product result =
        productService.updateProduct(productId, ProductForm.fromProduct(newProductData));

    // Then
    assertThat(result).isEqualTo(updatedProduct);
    assertThat(result.getName()).isEqualTo("Updated Product");

    verify(dtoValidator).validate(newProductData);
    verify(productSearchService).findById(productId);
    verify(productRepository)
        .save(
            argThat(
                product ->
                    product.getName().equals("Updated Product")
                        && product.getDescription().equals("Updated Description")));
    verify(productCache).put(productId, updatedProduct);
  }

  private Product createUpdatedTestProduct() {
    return Product.builder()
        .name("Updated Product")
        .description("Updated Description")
        .category("Updated Category")
        .brand("Updated Brand")
        .price(new BigDecimal("149.99"))
        .build();
  }

  private Product createUpdatedTestProduct(Long id) {
    return Product.builder()
        .id(id)
        .name("Updated Product")
        .description("Updated Description")
        .category("Updated Category")
        .brand("Updated Brand")
        .price(new BigDecimal("149.99"))
        .build();
  }

  @Test
  void updateProduct_ShouldThrowException_WhenIdIsNull() {
    // Given
    Product newProductData = createUpdatedTestProduct();
    ProductForm productForm = ProductForm.fromProduct(newProductData);
    doNothing().when(authService).requireAdmin();

    // When & Then
    assertThatThrownBy(() -> productService.updateProduct(null, productForm))
        .isInstanceOf(ValidationException.class)
        .hasMessage("Product ID cannot be null");

    verify(dtoValidator, never()).validate(any());
    verify(productRepository, never()).save(any());
  }

  @Test
  void updateProduct_ShouldThrowException_WhenProductNotFound() {
    // Given
    Long productId = 999L;
    Product newProductData = createUpdatedTestProduct();
    ProductForm productForm = ProductForm.fromProduct(newProductData);

    doNothing().when(authService).requireAdmin();
    doNothing().when(dtoValidator).validate(newProductData);
    when(productSearchService.findById(productId)).thenReturn(Optional.empty());

    // When & Then
    var asserted = assertThatThrownBy(() -> productService.updateProduct(productId, productForm));
    asserted.isInstanceOf(ResourceNotFoundException.class);
    asserted.extracting(e -> Long.parseLong(((ResourceNotFoundException) e).getId()))
                    .isEqualTo(productId);
    asserted.extracting(e -> ((ResourceNotFoundException) e).getResource())
                    .isEqualTo("product");

    verify(productRepository, never()).save(any());
  }

  @Test
  void updateProduct_ShouldThrowException_WhenNotAdmin() {
    // Given
    Long productId = 1L;
    Product newProductData = createUpdatedTestProduct();
    ProductForm productForm = ProductForm.fromProduct(newProductData);
    doThrow(new SecurityException("Admin access required")).when(authService).requireAdmin();

    // When & Then
    assertThatThrownBy(() -> productService.updateProduct(productId, productForm))
        .isInstanceOf(SecurityException.class)
        .hasMessage("Admin access required");

    verify(dtoValidator, never()).validate(any());
    verify(productRepository, never()).save(any());
  }

  @Test
  void updateProduct_ShouldThrowException_WhenValidationFails() {
    // Given
    Long productId = 1L;
    Product newProductData = createUpdatedTestProduct();
    ProductForm productForm = ProductForm.fromProduct(newProductData);
    Product existingProduct = createTestProduct(productId);

    doNothing().when(authService).requireAdmin();
    doThrow(new IllegalArgumentException("Invalid product data"))
        .when(dtoValidator)
        .validate(newProductData);
    when(productSearchService.findById(productId)).thenReturn(Optional.of(existingProduct));

    // When & Then
    assertThatThrownBy(() -> productService.updateProduct(productId, productForm))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Invalid product data");

    verify(productRepository, never()).save(any());
  }

  @Test
  void findById_ShouldDelegateToProductSearchService() {
    // Given
    Long productId = 1L;
    Product expectedProduct = createTestProduct(productId);
    when(productSearchService.findById(productId)).thenReturn(Optional.of(expectedProduct));

    // When
    Optional<Product> result = productService.findById(productId);

    // Then
    assertThat(result).contains(expectedProduct);
    verify(productSearchService).findById(productId);
  }

  @Test
  void clearCache_ShouldClearCacheSuccessfully() {
    // Given
    doNothing().when(authService).requireAdmin();

    // When
    productService.clearCache();

    // Then
    verify(authService).requireAdmin();
    verify(productCache).clear();
  }

  @Test
  void clearCache_ShouldThrowException_WhenNotAdmin() {
    // Given
    doThrow(new SecurityException("Admin access required")).when(authService).requireAdmin();

    // When & Then
    assertThatThrownBy(() -> productService.clearCache())
        .isInstanceOf(SecurityException.class)
        .hasMessage("Admin access required");

    verify(productCache, never()).clear();
  }

  @Test
  void updateProduct_ShouldPreserveId_WhenBuildingForUpdate() {
    // Given
    Long productId = 1L;
    Product existingProduct = createTestProduct(productId);
    Product newProductData = createUpdatedTestProduct();

    doNothing().when(authService).requireAdmin();
    doNothing().when(dtoValidator).validate(newProductData);
    when(productSearchService.findById(productId)).thenReturn(Optional.of(existingProduct));
    when(productRepository.save(any(Product.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // When
    Product result =
        productService.updateProduct(productId, ProductForm.fromProduct(newProductData));

    // Then
    assertThat(result.getId()).isEqualTo(productId);
    verify(productRepository)
        .save(
            argThat(
                product ->
                    product.getId().equals(productId)
                        && product.getName().equals("Updated Product")));
  }

  @Test
  void addProduct_ShouldCreateNewProductInstance_UsingBuilder() {
    // Given
    Product inputProduct = createTestProduct(null);
    Product savedProduct = createTestProduct(1L);

    doNothing().when(authService).requireAdmin();
    doNothing().when(dtoValidator).validate(inputProduct);
    when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
    when(authService.getAdminUserName()).thenReturn("admin");

    // When
    productService.addProduct(inputProduct);

    // Then
    verify(productRepository)
        .save(argThat(product -> product != inputProduct && product.getId() == null));
  }
}

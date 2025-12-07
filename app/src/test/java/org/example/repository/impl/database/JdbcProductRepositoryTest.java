package org.example.repository.impl.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.example.configuration.LiquibaseConfigurationUpdater;
import org.example.exception.DataAccessException;
import org.example.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(
    classes = {
      JdbcProductRepository.class,
      ConnectionManager.class,
      LiquibaseConfigurationUpdater.class
    })
class JdbcProductRepositoryTest extends BaseRepositoryTest {
  @Autowired JdbcProductRepository productRepository;

  @Test
  void save_ShouldInsertNewProductAndReturnWithGeneratedId() {
    // Given
    Product product =
        createTestProduct(
            "Laptop", "Gaming laptop", "Electronics", "Dell", new BigDecimal("999.99"));

    // When
    Product savedProduct = productRepository.save(product);

    // Then
    assertThat(savedProduct).isNotNull();
    assertThat(savedProduct.getId()).isNotNull().isPositive();
    assertThat(savedProduct.getName()).isEqualTo("Laptop");
    assertThat(savedProduct.getDescription()).isEqualTo("Gaming laptop");
    assertThat(savedProduct.getCategory()).isEqualTo("Electronics");
    assertThat(savedProduct.getBrand()).isEqualTo("Dell");
    assertThat(savedProduct.getPrice()).isEqualByComparingTo("999.99");
  }

  private Product createTestProduct(
      String name, String description, String category, String brand, BigDecimal price) {
    Product product = new Product();
    product.setName(name);
    product.setDescription(description);
    product.setCategory(category);
    product.setBrand(brand);
    product.setPrice(price);
    return product;
  }

  @Test
  void save_ShouldUpdateExistingProduct() {
    // Given
    Product product =
        createTestProduct(
            "Phone", "Smartphone", "Electronics", "Samsung", new BigDecimal("499.99"));
    Product savedProduct = productRepository.save(product);

    // When - update the product
    savedProduct.setName("Updated Phone");
    savedProduct.setDescription("Updated smartphone");
    savedProduct.setPrice(new BigDecimal("449.99"));
    Product updatedProduct = productRepository.save(savedProduct);

    // Then
    assertThat(updatedProduct.getId()).isEqualTo(savedProduct.getId());
    assertThat(updatedProduct.getName()).isEqualTo("Updated Phone");
    assertThat(updatedProduct.getDescription()).isEqualTo("Updated smartphone");
    assertThat(updatedProduct.getPrice()).isEqualByComparingTo("449.99");

    // Verify the update persisted
    Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());
    assertThat(foundProduct).isPresent();
    assertThat(foundProduct.get().getName()).isEqualTo("Updated Phone");
  }

  @Test
  void findById_ShouldReturnProduct_WhenProductExists() {
    // Given
    Product product =
        createTestProduct(
            "Tablet", "Android tablet", "Electronics", "Samsung", new BigDecimal("299.99"));
    Product savedProduct = productRepository.save(product);

    // When
    Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

    // Then
    assertThat(foundProduct).isPresent();
    assertThat(foundProduct.get().getId()).isEqualTo(savedProduct.getId());
    assertThat(foundProduct.get().getName()).isEqualTo("Tablet");
    assertThat(foundProduct.get().getBrand()).isEqualTo("Samsung");
  }

  @Test
  void findById_ShouldReturnEmpty_WhenProductDoesNotExist() {
    // When
    Optional<Product> foundProduct = productRepository.findById(999L);

    // Then
    assertThat(foundProduct).isEmpty();
  }

  @Test
  void findAll_ShouldReturnAllProducts() {
    // Given
    Product product1 =
        createTestProduct(
            "Laptop", "Gaming laptop", "Electronics", "Dell", new BigDecimal("999.99"));
    Product product2 =
        createTestProduct(
            "Mouse", "Wireless mouse", "Electronics", "Logitech", new BigDecimal("29.99"));
    Product product3 =
        createTestProduct(
            "Book", "Java programming", "Books", "TechPress", new BigDecimal("39.99"));

    productRepository.save(product1);
    productRepository.save(product2);
    productRepository.save(product3);

    // When
    List<Product> allProducts = productRepository.findAll();

    // Then
    assertThat(allProducts).hasSize(3);
    assertThat(allProducts)
        .extracting(Product::getName)
        .containsExactlyInAnyOrder("Laptop", "Mouse", "Book");
  }

  @Test
  void findAll_ShouldReturnEmptyList_WhenNoProductsExist() {
    // When
    List<Product> products = productRepository.findAll();

    // Then
    assertThat(products).isEmpty();
  }

  @Test
  void delete_ShouldReturnTrue_WhenProductExists() {
    // Given
    Product product =
        createTestProduct(
            "Keyboard", "Mechanical keyboard", "Electronics", "Corsair", new BigDecimal("89.99"));
    Product savedProduct = productRepository.save(product);

    // When
    boolean deleted = productRepository.delete(savedProduct.getId());

    // Then
    assertThat(deleted).isTrue();

    // Verify product is actually deleted
    Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());
    assertThat(foundProduct).isEmpty();
  }

  @Test
  void delete_ShouldReturnFalse_WhenProductDoesNotExist() {
    // When
    boolean deleted = productRepository.delete(999L);

    // Then
    assertThat(deleted).isFalse();
  }

  @Test
  void searchByName_ShouldReturnProducts_WhenNameMatches() {
    // Given
    Product product1 =
        createTestProduct(
            "Gaming Laptop",
            "High-end gaming laptop",
            "Electronics",
            "Dell",
            new BigDecimal("1299.99"));
    Product product2 =
        createTestProduct(
            "Office Laptop", "Business laptop", "Electronics", "Lenovo", new BigDecimal("899.99"));
    Product product3 =
        createTestProduct(
            "Gaming Mouse", "RGB gaming mouse", "Electronics", "Razer", new BigDecimal("59.99"));

    productRepository.save(product1);
    productRepository.save(product2);
    productRepository.save(product3);

    // When
    List<Product> gamingProducts = productRepository.searchByName("gaming");

    // Then
    assertThat(gamingProducts).hasSize(2);
    assertThat(gamingProducts)
        .extracting(Product::getName)
        .containsExactlyInAnyOrder("Gaming Laptop", "Gaming Mouse");
  }

  @Test
  void searchByName_ShouldReturnEmptyList_WhenNoMatches() {
    // Given
    Product product =
        createTestProduct(
            "Laptop", "Standard laptop", "Electronics", "Dell", new BigDecimal("799.99"));
    productRepository.save(product);

    // When
    List<Product> results = productRepository.searchByName("nonexistent");

    // Then
    assertThat(results).isEmpty();
  }

  @Test
  void filterByCategory_ShouldReturnProducts_WhenCategoryMatches() {
    // Given
    Product product1 =
        createTestProduct(
            "Laptop", "Gaming laptop", "Electronics", "Dell", new BigDecimal("999.99"));
    Product product2 =
        createTestProduct(
            "Java Book", "Programming book", "Books", "O'Reilly", new BigDecimal("49.99"));
    Product product3 =
        createTestProduct(
            "Mouse", "Wireless mouse", "Electronics", "Logitech", new BigDecimal("29.99"));

    productRepository.save(product1);
    productRepository.save(product2);
    productRepository.save(product3);

    // When
    List<Product> electronics = productRepository.filterByCategory("Electronics");

    // Then
    assertThat(electronics).hasSize(2);
    assertThat(electronics)
        .extracting(Product::getName)
        .containsExactlyInAnyOrder("Laptop", "Mouse");
  }

  @Test
  void filterByBrand_ShouldReturnProducts_WhenBrandMatches() {
    // Given
    Product product1 =
        createTestProduct("Laptop1", "Laptop", "Electronics", "Dell", new BigDecimal("999.99"));
    Product product2 =
        createTestProduct("Laptop2", "Laptop", "Electronics", "HP", new BigDecimal("899.99"));
    Product product3 =
        createTestProduct("Monitor", "Monitor", "Electronics", "Dell", new BigDecimal("299.99"));

    productRepository.save(product1);
    productRepository.save(product2);
    productRepository.save(product3);

    // When
    List<Product> dellProducts = productRepository.filterByBrand("Dell");

    // Then
    assertThat(dellProducts).hasSize(2);
    assertThat(dellProducts)
        .extracting(Product::getName)
        .containsExactlyInAnyOrder("Laptop1", "Monitor");
  }

  @Test
  void filterByPriceRange_ShouldReturnProducts_WithinPriceRange() {
    // Given
    Product product1 =
        createTestProduct("Cheap Mouse", "Mouse", "Electronics", "BrandA", new BigDecimal("19.99"));
    Product product2 =
        createTestProduct("Mid Mouse", "Mouse", "Electronics", "BrandB", new BigDecimal("49.99"));
    Product product3 =
        createTestProduct(
            "Expensive Mouse", "Mouse", "Electronics", "BrandC", new BigDecimal("99.99"));
    Product product4 =
        createTestProduct(
            "Very Expensive Mouse", "Mouse", "Electronics", "BrandD", new BigDecimal("199.99"));

    productRepository.save(product1);
    productRepository.save(product2);
    productRepository.save(product3);
    productRepository.save(product4);

    // When
    List<Product> midRangeProducts =
        productRepository.filterByPriceRange(new BigDecimal("30.00"), new BigDecimal("100.00"));

    // Then
    assertThat(midRangeProducts).hasSize(2);
    assertThat(midRangeProducts)
        .extracting(Product::getName)
        .containsExactlyInAnyOrder("Mid Mouse", "Expensive Mouse");
  }

  @Test
  void filterByPriceRange_ShouldReturnEmptyList_WhenNoProductsInRange() {
    // Given
    Product product =
        createTestProduct("Product", "Description", "Category", "Brand", new BigDecimal("100.00"));
    productRepository.save(product);

    // When
    List<Product> results =
        productRepository.filterByPriceRange(new BigDecimal("200.00"), new BigDecimal("300.00"));

    // Then
    assertThat(results).isEmpty();
  }

  @Test
  void save_ShouldThrowDataAccessException_WhenInsertFails() {
    // Given
    Product product =
        createTestProduct(null, "Description", "Category", "Brand", new BigDecimal("99.99"));

    // When & Then
    assertThatThrownBy(() -> productRepository.save(product))
        .isInstanceOf(DataAccessException.class)
        .hasMessageContaining("Failed to insert product");
  }
}

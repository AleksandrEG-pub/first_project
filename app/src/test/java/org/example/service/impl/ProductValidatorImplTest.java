package org.example.service.impl;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import org.example.model.Product;
import org.example.service.DtoValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class ProductValidatorImplTest {

  private final DtoValidator dtoValidator;

  {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      dtoValidator = new DtoValidatorImpl(factory.getValidator());
    }
  }

  @Test
  void validateProductData_ShouldNotThrowException_WhenProductIsValid() {
    // Given
    Product validProduct = createValidProduct();

    // When & Then
    assertThatNoException().isThrownBy(() -> dtoValidator.validate(validProduct));
  }

  private Product createValidProduct() {
    Product product = new Product();
    product.setName("Laptop");
    product.setDescription("Gaming laptop with high performance");
    product.setCategory("Electronics");
    product.setBrand("Dell");
    product.setPrice(new BigDecimal("999.99"));
    return product;
  }

  @Test
  void validateProductData_ShouldThrowException_WhenProductNameIsNull() {
    // Given
    Product product = createValidProduct();
    product.setName(null);

    // When & Then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> dtoValidator.validate(product))
        .withMessage("Product name cannot be null or empty");
  }

  @Test
  void validateProductData_ShouldThrowException_WhenProductNameIsEmpty() {
    // Given
    Product product = createValidProduct();
    product.setName("");

    // When & Then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> dtoValidator.validate(product))
        .withMessage("Product name cannot be null or empty");
  }

  @Test
  void validateProductData_ShouldThrowException_WhenProductNameIsBlank() {
    // Given
    Product product = createValidProduct();
    product.setName("   ");

    // When & Then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> dtoValidator.validate(product))
        .withMessage("Product name cannot be null or empty");
  }

  @Test
  void validateProductData_ShouldThrowException_WhenProductDescriptionIsNull() {
    // Given
    Product product = createValidProduct();
    product.setDescription(null);

    // When & Then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> dtoValidator.validate(product))
        .withMessage("Product description cannot be null or empty");
  }

  @Test
  void validateProductData_ShouldThrowException_WhenProductDescriptionIsEmpty() {
    // Given
    Product product = createValidProduct();
    product.setDescription("");

    // When & Then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> dtoValidator.validate(product))
        .withMessage("Product description cannot be null or empty");
  }

  @Test
  void validateProductData_ShouldThrowException_WhenProductDescriptionIsBlank() {
    // Given
    Product product = createValidProduct();
    product.setDescription("   ");

    // When & Then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> dtoValidator.validate(product))
        .withMessage("Product description cannot be null or empty");
  }

  @Test
  void validateProductData_ShouldThrowException_WhenProductCategoryIsNull() {
    // Given
    Product product = createValidProduct();
    product.setCategory(null);

    // When & Then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> dtoValidator.validate(product))
        .withMessage("Product category cannot be null or empty");
  }

  @Test
  void validateProductData_ShouldThrowException_WhenProductCategoryIsEmpty() {
    // Given
    Product product = createValidProduct();
    product.setCategory("");

    // When & Then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> dtoValidator.validate(product))
        .withMessage("Product category cannot be null or empty");
  }

  @Test
  void validateProductData_ShouldThrowException_WhenProductCategoryIsBlank() {
    // Given
    Product product = createValidProduct();
    product.setCategory("   ");

    // When & Then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> dtoValidator.validate(product))
        .withMessage("Product category cannot be null or empty");
  }

  @Test
  void validateProductData_ShouldThrowException_WhenProductBrandIsNull() {
    // Given
    Product product = createValidProduct();
    product.setBrand(null);

    // When & Then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> dtoValidator.validate(product))
        .withMessage("Product brand cannot be null or empty");
  }

  @Test
  void validateProductData_ShouldThrowException_WhenProductBrandIsEmpty() {
    // Given
    Product product = createValidProduct();
    product.setBrand("");

    // When & Then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> dtoValidator.validate(product))
        .withMessage("Product brand cannot be null or empty");
  }

  @Test
  void validateProductData_ShouldThrowException_WhenProductBrandIsBlank() {
    // Given
    Product product = createValidProduct();
    product.setBrand("   ");

    // When & Then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> dtoValidator.validate(product))
        .withMessage("Product brand cannot be null or empty");
  }

  @Test
  void validateProductData_ShouldThrowException_WhenProductPriceIsNull() {
    // Given
    Product product = createValidProduct();
    product.setPrice(null);

    // When & Then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> dtoValidator.validate(product))
        .withMessage("Product price cannot be null");
  }

  @Test
  void validateProductData_ShouldThrowException_WhenProductPriceIsZero() {
    // Given
    Product product = createValidProduct();
    product.setPrice(BigDecimal.ZERO);

    // When & Then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> dtoValidator.validate(product))
        .withMessage("Product price must be greater than zero");
  }

  @Test
  void validateProductData_ShouldThrowException_WhenProductPriceIsNegative() {
    // Given
    Product product = createValidProduct();
    product.setPrice(new BigDecimal("-10.50"));

    // When & Then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> dtoValidator.validate(product))
        .withMessage("Product price must be greater than zero");
  }

  @Test
  void validateProductData_ShouldAcceptValidPriceWithDifferentScales() {
    // Given
    Product product1 = createValidProduct();
    product1.setPrice(new BigDecimal("25.00"));

    Product product2 = createValidProduct();
    product2.setPrice(new BigDecimal("19.99"));

    Product product3 = createValidProduct();
    product3.setPrice(new BigDecimal("100"));

    // When & Then - All should not throw exceptions
    assertThatNoException().isThrownBy(() -> dtoValidator.validate(product1));
    assertThatNoException().isThrownBy(() -> dtoValidator.validate(product2));
    assertThatNoException().isThrownBy(() -> dtoValidator.validate(product3));
  }

  @Test
  void validateProductData_ShouldThrowFirstValidationError_WhenMultipleFieldsAreInvalid() {
    // Given
    Product product = createValidProduct();
    product.setName(null);
    product.setDescription(null);
    product.setPrice(null);

    // When & Then - Should throw for the first invalid field (name)
    assertThatIllegalArgumentException()
        .isThrownBy(() -> dtoValidator.validate(product))
        .withMessage("Product name cannot be null or empty");
  }

  @ParameterizedTest
  @CsvSource({
    "254, false, ''",
    "255, true, 'Product name cannot be longer than 255'",
    "300, true, 'Product name cannot be longer than 255'",
    "1, false, ''",
    "100, false, ''",
  })
  void validateProductData_ShouldValidateNameLength(
      int nameLength, boolean shouldThrow, String expectedMessage) {
    // Given
    Product product = createValidProduct();
    product.setName("A".repeat(nameLength));

    // When & Then
    if (shouldThrow) {
      assertThatIllegalArgumentException()
          .isThrownBy(() -> dtoValidator.validate(product))
          .withMessage(expectedMessage);
    } else {
      assertThatNoException().isThrownBy(() -> dtoValidator.validate(product));
    }
  }

  @ParameterizedTest
  @CsvSource({
    "9999, false, ''",
    "10000, true, 'Product description cannot be longer than 10000'",
    "15000, true, 'Product description cannot be longer than 10000'",
    "1, false, ''",
    "5000, false, ''",
  })
  void validateProductData_ShouldValidateDescriptionLength(
      int descLength, boolean shouldThrow, String expectedMessage) {
    // Given
    Product product = createValidProduct();
    product.setDescription("B".repeat(descLength));

    // When & Then
    if (shouldThrow) {
      assertThatIllegalArgumentException()
          .isThrownBy(() -> dtoValidator.validate(product))
          .withMessage(expectedMessage);
    } else {
      assertThatNoException().isThrownBy(() -> dtoValidator.validate(product));
    }
  }

  @ParameterizedTest
  @CsvSource({
    "254, false, ''",
    "255, true, 'Product category cannot be longer than 255'",
    "300, true, 'Product category cannot be longer than 255'",
    "1, false, ''",
    "100, false, ''",
  })
  void validateProductData_ShouldValidateCategoryLength(
      int categoryLength, boolean shouldThrow, String expectedMessage) {
    // Given
    Product product = createValidProduct();
    product.setCategory("C".repeat(categoryLength));

    // When & Then
    if (shouldThrow) {
      assertThatIllegalArgumentException()
          .isThrownBy(() -> dtoValidator.validate(product))
          .withMessage(expectedMessage);
    } else {
      assertThatNoException().isThrownBy(() -> dtoValidator.validate(product));
    }
  }

  @ParameterizedTest
  @CsvSource({
    "254, false, ''",
    "255, true, 'Product brand cannot be longer than 255'",
    "300, true, 'Product brand cannot be longer than 255'",
    "1, false, ''",
    "100, false, ''",
  })
  void validateProductData_ShouldValidateBrandLength(
      int brandLength, boolean shouldThrow, String expectedMessage) {
    // Given
    Product product = createValidProduct();
    product.setBrand("D".repeat(brandLength));

    // When & Then
    if (shouldThrow) {
      assertThatIllegalArgumentException()
          .isThrownBy(() -> dtoValidator.validate(product))
          .withMessage(expectedMessage);
    } else {
      assertThatNoException().isThrownBy(() -> dtoValidator.validate(product));
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"0.01", "0.02", "1.00", "100.50", "999999.99"})
  void validateProductData_ShouldAcceptValidPrices(String validPrice) {
    // Given
    Product product = createValidProduct();
    product.setPrice(new BigDecimal(validPrice));

    // When & Then
    assertThatNoException().isThrownBy(() -> dtoValidator.validate(product));
  }

  @ParameterizedTest
  @CsvSource({
    "0.00, 'Product price must be greater than zero'",
    "-1.00, 'Product price must be greater than zero'",
    "-0.01, 'Product price must be greater than zero'",
    "0.009, 'Product price must be greater than zero'",
  })
  void validateProductData_ShouldRejectInvalidPrices(String invalidPrice, String expectedMessage) {
    // Given
    Product product = createValidProduct();
    product.setPrice(new BigDecimal(invalidPrice));

    // When & Then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> dtoValidator.validate(product))
        .withMessage(expectedMessage);
  }
}

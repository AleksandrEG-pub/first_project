package org.example.service.impl;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.service.DtoValidator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DtoValidatorImpl implements DtoValidator {
  private final Validator validator;

  @Override
  public void validate(Object object) {
    var constraintViolations = validator.validate(object);
    if (!constraintViolations.isEmpty()) {
      String messages =
          constraintViolations.stream()
              .map(ConstraintViolation::getMessage)
              .map("[%s]"::formatted)
              .collect(Collectors.joining(","));
      throw new ValidationException("Constraint violations: " + messages);
    }
  }
}

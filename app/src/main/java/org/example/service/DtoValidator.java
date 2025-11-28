package org.example.service;


/** Validation for products according to business rules */
public interface DtoValidator {
  /** Full validation for all business rules related to product */
  void validate(Object object);
}

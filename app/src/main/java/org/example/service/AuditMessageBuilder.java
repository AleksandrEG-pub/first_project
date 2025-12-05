package org.example.service;

public interface AuditMessageBuilder {
  String getUsername();

  String buildSearchMessage(Object criteriaObject, Object resultProductList);
}

package org.example.service;

/** Defines messages for audit operations */
public interface AuditMessageBuilder {
  /** who performed operations */
  String getUsername();

  /**
   * creates an audit message for search by criteria operation
   *
   * @param criteriaObject - criteria search details
   * @param resultProductList - result of the search
   */
  String buildSearchMessage(Object criteriaObject, Object resultProductList);
}

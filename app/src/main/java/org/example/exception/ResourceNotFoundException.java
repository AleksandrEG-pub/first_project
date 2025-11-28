package org.example.exception;

import lombok.Getter;

/**
 * Thrown when search for particular resource by id is happened and target resource does not exist
 * in system.
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {
  /** name of resource, e.g. product or user */
  private final String resource;

  /** which id was used for resource search */
  private final String id;

  public ResourceNotFoundException(String resource, String id) {
    this.resource = resource;
    this.id = id;
  }
}

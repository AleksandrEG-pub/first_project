package org.example.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
  private final String resource;
  private final String id;

  public ResourceNotFoundException(String resource, String id) {
    this.resource = resource;
    this.id = id;
  }
}

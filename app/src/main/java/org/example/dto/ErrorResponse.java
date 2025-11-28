package org.example.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Standard response for all exceptions happened during http requests to server
 *
 * <p>Partial implementation of RFC 9457
 */
@Getter
@Builder
public class ErrorResponse {
  @Builder.Default String type = "about:blank";
  String title;
  int status;
  String instance;
}

package org.example.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
  String type;
  @Builder.Default
  String title = "about:blank";
  int status;
  String instance;
}

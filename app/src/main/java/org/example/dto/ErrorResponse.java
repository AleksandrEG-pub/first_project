package org.example.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
  @Builder.Default String type = "about:blank";
  String title;
  int status;
  String instance;
}

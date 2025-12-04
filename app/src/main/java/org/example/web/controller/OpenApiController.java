package org.example.web.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OpenApiController {

  @GetMapping(value = "/v3/api-docs")
  public ResponseEntity<String> apiDocs() throws Exception {
    try {
      Path path = Paths.get(getClass().getResource("/openapi.yaml").toURI());
      return ResponseEntity.ok(Files.readString(path));
    } catch (Exception e) {
      throw new RuntimeException("Failed to load OpenAPI spec", e);
    }
  }
}

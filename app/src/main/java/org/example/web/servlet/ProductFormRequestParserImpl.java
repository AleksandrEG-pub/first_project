package org.example.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.example.dto.ProductForm;
import org.example.exception.ValidationException;

public class ProductFormRequestParserImpl implements ProductFormRequestParser {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public ProductForm parse(HttpServletRequest req) {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()))) {
      return objectMapper.readValue(reader, ProductForm.class);
    } catch (IOException e) {
      throw new ValidationException("incorrect product form format");
    }
  }
}

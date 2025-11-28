package org.example.web.configuration.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.service.AuditService;
import org.example.service.DtoValidator;
import org.example.service.ProductFormRequestParser;
import org.example.service.ProductService;
import org.example.service.impl.CriteriaRequestParserImpl;
import org.example.web.configuration.ServletMapping;
import org.example.web.servlet.AuditServlet;
import org.example.web.servlet.ProductServlet;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServletMappingImpl implements ServletMapping {
  private final AuditService auditService;
  private final ProductService productService;
  private final CriteriaRequestParserImpl criteriaRequestParser;
  private final ProductFormRequestParser productFormRequestParser;
  private final DtoValidator dtoValidator;
  private final ObjectMapper objectMapper;

  @Override
  public Map<String, HttpServlet> getServletMapping() {

    return Map.of(
        "/products/*",
            new ProductServlet(
                productService,
                criteriaRequestParser,
                productFormRequestParser,
                dtoValidator,
                objectMapper),
        "/audits/*", new AuditServlet(auditService, objectMapper));
  }
}

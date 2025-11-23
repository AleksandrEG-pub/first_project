package org.example.web.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import java.util.Map;
import org.example.service.AuditService;
import org.example.service.DtoValidator;
import org.example.service.ProductService;
import org.example.web.servlet.AuditServlet;
import org.example.service.CriteriaRequestParserImpl;
import org.example.service.ProductFormRequestParser;
import org.example.service.ProductFormRequestParserImpl;
import org.example.web.servlet.ProductServlet;

public class ServletMappingImpl implements ServletMapping {
  private final AuditService auditService;
  private final ProductService productService;
  private final CriteriaRequestParserImpl criteriaRequestParser = new CriteriaRequestParserImpl();
  private final ProductFormRequestParser productFormRequestParser =
      new ProductFormRequestParserImpl();
  private final DtoValidator dtoValidator;
  private final ObjectMapper objectMapper;

  public ServletMappingImpl(
      AuditService auditService,
      ProductService productService,
      DtoValidator dtoValidator,
      ObjectMapper objectMapper) {
    this.auditService = auditService;
    this.productService = productService;
    this.dtoValidator = dtoValidator;
    this.objectMapper = objectMapper;
  }

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

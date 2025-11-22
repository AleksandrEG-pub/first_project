package org.example.web.configuration;

import jakarta.servlet.http.HttpServlet;
import org.example.service.ProductService;
import org.example.web.servlet.AuditServlet;
import org.example.web.servlet.AuthenticationServlet;
import org.example.web.servlet.CriteriaRequestParserImpl;
import org.example.web.servlet.ProductFormRequestParser;
import org.example.web.servlet.ProductFormRequestParserImpl;
import org.example.web.servlet.ProductServlet;

import java.util.Map;

public class ServletMappingImpl implements ServletMapping {
  private final ProductService productService;
  private final CriteriaRequestParserImpl criteriaRequestParser = new CriteriaRequestParserImpl();
  private final ProductFormRequestParser productFormRequestParser =
      new ProductFormRequestParserImpl();

  public ServletMappingImpl(ProductService productService) {
    this.productService = productService;
  }

  @Override
  public Map<String, HttpServlet> getServletMapping() {

    return Map.of(
        "/products/*", new ProductServlet(productService, criteriaRequestParser, productFormRequestParser),
        "/audits/*", new AuditServlet(),
        "/auth/*", new AuthenticationServlet());
  }
}

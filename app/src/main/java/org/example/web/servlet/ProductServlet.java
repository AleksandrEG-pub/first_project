package org.example.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.example.dto.ProductForm;
import org.example.exception.ApplicationException;
import org.example.exception.ResourceNotFoundException;
import org.example.exception.ValidationException;
import org.example.model.Product;
import org.example.service.ProductService;
import org.example.service.SearchCriteria;

public class ProductServlet extends HttpServlet {
  /** Pattern: /123 (get by ID) */
  private final Pattern idPathParamPattern = Pattern.compile("/\\d+");

  private final transient ProductService productService;
  private final transient CriteriaRequestParser criteriaRequestParser;
  private final transient ProductFormRequestParser productFormRequestParser;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public ProductServlet(
      ProductService productService,
      CriteriaRequestParser criteriaRequestParser,
      ProductFormRequestParser productFormRequestParser) {
    this.productService = productService;
    this.criteriaRequestParser = criteriaRequestParser;
    this.productFormRequestParser = productFormRequestParser;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    String pathInfo = req.getPathInfo();
    if (isPathInfoHasId(pathInfo)) {
      handleGetById(req, resp);
    } else if (hasSearchParameters(req)) {
      handleSearch(req, resp);
    } else {
      handleGetAll(resp);
    }
  }

  private boolean isPathInfoHasId(String pathInfo) {
    return pathInfo != null && idPathParamPattern.matcher(pathInfo).matches();
  }

  private void handleGetById(HttpServletRequest req, HttpServletResponse resp) {
    Long productId = extractIdFromRequest(req);
    productService
        .findById(productId)
        .ifPresentOrElse(
            product -> writeResponseJson(resp, product, HttpServletResponse.SC_OK),
            () -> {
              throw new ResourceNotFoundException("product", String.valueOf(productId));
            });
  }

  private boolean hasSearchParameters(HttpServletRequest req) {
    return Stream.of("id", "name", "category", "brand", "minPrice", "maxPrice")
        .anyMatch(param -> req.getParameter(param) != null);
  }

  private void handleSearch(HttpServletRequest req, HttpServletResponse resp) {
    SearchCriteria criteria = criteriaRequestParser.buildSearchCriteria(req);
    List<Product> products = productService.search(criteria);
    writeResponseJson(resp, products, HttpServletResponse.SC_OK);
  }

  private void handleGetAll(HttpServletResponse resp) {
    List<Product> products = productService.getAllProducts();
    writeResponseJson(resp, products, HttpServletResponse.SC_OK);
  }

  private Long extractIdFromRequest(HttpServletRequest req) {
    String pathInfo = req.getPathInfo();
    if (isPathInfoHasId(pathInfo)) {
      return Long.valueOf(pathInfo.substring(1));
    }
    throw new ValidationException("Product ID is required");
  }

  private void writeResponseJson(HttpServletResponse resp, Object object, int status) {
    resp.setContentType("application/json");
    resp.setStatus(status);
    try (PrintWriter writer = resp.getWriter()) {
      String json = objectMapper.writer().writeValueAsString(object);
      writer.write(json);
    } catch (IOException e) {
      throw new ApplicationException("Failed to write response", e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
    ProductForm productForm = productFormRequestParser.parse(req);
    Product createdProduct = productService.create(productForm);
    writeResponseJson(resp, createdProduct, HttpServletResponse.SC_CREATED);
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
    Long productId = extractIdFromRequest(req);
    ProductForm updatedData = productFormRequestParser.parse(req);
    Product updatedProduct = productService.updateProduct(productId, updatedData);
    writeResponseJson(resp, updatedProduct, HttpServletResponse.SC_OK);
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
    Long productId = extractIdFromRequest(req);
    productService.deleteProduct(productId);
    resp.setStatus(HttpServletResponse.SC_OK);
  }
}

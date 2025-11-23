package org.example.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Stream;
import org.example.dto.ProductForm;
import org.example.exception.ApplicationException;
import org.example.exception.MissingRequestParameterException;
import org.example.exception.ParameterTypeMismatchException;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.ProductMapper;
import org.example.model.Product;
import org.example.service.DtoValidator;
import org.example.service.ProductService;
import org.example.dto.SearchCriteria;
import org.example.web.RequestPathTools;

public class ProductServlet extends HttpServlet {
  private static final ProductMapper PRODUCT_MAPPER = ProductMapper.INSTANCE;
  private final transient ProductService productService;
  private final transient CriteriaRequestParser criteriaRequestParser;
  private final transient ProductFormRequestParser productFormRequestParser;
  private final transient DtoValidator dtoValidator;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public ProductServlet(
      ProductService productService,
      CriteriaRequestParser criteriaRequestParser,
      ProductFormRequestParser productFormRequestParser,
      DtoValidator dtoValidator) {
    this.productService = productService;
    this.criteriaRequestParser = criteriaRequestParser;
    this.productFormRequestParser = productFormRequestParser;
    this.dtoValidator = dtoValidator;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    String pathInfo = req.getPathInfo();
    if (RequestPathTools.isPathInfoHasId(pathInfo)) {
      handleGetById(req, resp);
    } else if (hasSearchParameters(req)) {
      handleSearch(req, resp);
    } else {
      handleGetAll(resp);
    }
  }

  private void handleGetById(HttpServletRequest req, HttpServletResponse resp) {
    Long productId = extractIdFromRequest(req);
    productService
        .findById(productId)
        .map(PRODUCT_MAPPER::toDto)
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
    var products = productService.search(criteria).stream().map(PRODUCT_MAPPER::toDto).toList();
    writeResponseJson(resp, products, HttpServletResponse.SC_OK);
  }

  private void handleGetAll(HttpServletResponse resp) {
    var products = productService.getAllProducts().stream().map(PRODUCT_MAPPER::toDto).toList();
    writeResponseJson(resp, products, HttpServletResponse.SC_OK);
  }

  private Long extractIdFromRequest(HttpServletRequest req) {
    String pathInfo = req.getPathInfo();
    if (RequestPathTools.isPathInfoHasId(pathInfo)) {
      String idStr = pathInfo.substring(1);
      try {
        long id = Long.parseLong(idStr);
        if (id < 0) {
          throw new ValidationException("id can not be negative");
        }
        return id;
      } catch (NumberFormatException e) {
        throw new ParameterTypeMismatchException("Can not convert to integer: " + idStr, e);
      }
    }
    throw new MissingRequestParameterException("Product ID is required");
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
    dtoValidator.validate(productForm);
    Product createdProduct = productService.create(productForm);
    writeResponseJson(resp, createdProduct, HttpServletResponse.SC_CREATED);
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
    Long productId = extractIdFromRequest(req);
    ProductForm updatedData = productFormRequestParser.parse(req);
    dtoValidator.validate(updatedData);
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

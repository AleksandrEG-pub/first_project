package org.example.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import org.example.service.SearchCriteria;

public class CriteriaRequestParserImpl implements CriteriaRequestParser {
  @Override
  public SearchCriteria buildSearchCriteria(HttpServletRequest req) {
    var builder = new SearchCriteria.Builder();
    String id = req.getParameter("id");
    if (id != null && !id.trim().isEmpty()) {
      builder.id(Long.valueOf(id));
    }
    String name = req.getParameter("name");
    if (name != null && !name.trim().isEmpty()) {
      builder.name(name);
    }
    String category = req.getParameter("category");
    if (category != null && !category.trim().isEmpty()) {
      builder.category(category);
    }
    String brand = req.getParameter("brand");
    if (brand != null && !brand.trim().isEmpty()) {
      builder.brand(brand);
    }
    String minPrice = req.getParameter("minPrice");
    if (minPrice != null && !minPrice.trim().isEmpty()) {
      builder.minPrice(new BigDecimal(minPrice));
    }
    String maxPrice = req.getParameter("maxPrice");
    if (maxPrice != null && !maxPrice.trim().isEmpty()) {
      builder.minPrice(new BigDecimal(maxPrice));
    }
    return builder.build();
  }
}

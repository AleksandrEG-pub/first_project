package org.example.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Optional;
import org.example.service.SearchCriteria;

public class CriteriaRequestParserImpl implements CriteriaRequestParser {
  @Override
  public SearchCriteria buildSearchCriteria(HttpServletRequest req) {
    var builder = new SearchCriteria.Builder();
    getParameter(req, "id").ifPresent(id -> builder.id(Long.valueOf(id)));
    getParameter(req, "name").ifPresent(builder::name);
    getParameter(req, "category").ifPresent(builder::category);
    getParameter(req, "brand").ifPresent(builder::brand);
    getParameter(req, "minPrice").ifPresent(price -> builder.minPrice(new BigDecimal(price)));
    getParameter(req, "maxPrice").ifPresent(price -> builder.maxPrice(new BigDecimal(price)));
    return builder.build();
  }

  /** Extracts and trims parameter value, returns empty if null or blank */
  private Optional<String> getParameter(HttpServletRequest req, String paramName) {
    return Optional.ofNullable(req.getParameter(paramName))
        .map(String::trim)
        .filter(s -> !s.isEmpty());
  }
}

package org.example.service.impl;

import java.util.List;
import org.example.dto.SearchCriteria;
import org.example.service.AuditMessageBuilder;
import org.springframework.stereotype.Component;

@Component(value = "auditMessageBuilder")
public class AuditMessageBuilderImpl implements AuditMessageBuilder {
  @Override
  public String getUsername() {
    return UserContext.getValidatedCurrentUser().getUsername();
  }

  @Override
  public String buildSearchMessage(Object criteriaObject, Object resultProductList) {
    if (criteriaObject instanceof SearchCriteria criteria
        && resultProductList instanceof List<?> productList) {
      return buildDetailedSearchMessage(criteria, productList.size());
    }
    String baseMessage = "Product search";
    if (resultProductList instanceof List<?> productList) {
      return (baseMessage + ", found: [%d]").formatted(productList.size());
    }
    return baseMessage;
  }

  private String buildDetailedSearchMessage(SearchCriteria criteria, int resultCount) {
    StringBuilder message = new StringBuilder("Search: ");
    if (criteria.getName() != null) {
      message.append("name='").append(criteria.getName()).append("' ");
    }
    if (criteria.getCategory() != null) {
      message.append("category='").append(criteria.getCategory()).append("' ");
    }
    if (criteria.getBrand() != null) {
      message.append("brand='").append(criteria.getBrand()).append("' ");
    }
    if (criteria.getMinPrice() != null || criteria.getMaxPrice() != null) {
      message
          .append("price[")
          .append(criteria.getMinPrice() != null ? criteria.getMinPrice() : "none")
          .append("-")
          .append(criteria.getMaxPrice() != null ? criteria.getMaxPrice() : "none")
          .append("] ");
    }
    message.append("- Found ").append(resultCount).append(" results");
    return message.toString();
  }
}

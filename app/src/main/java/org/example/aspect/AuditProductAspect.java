package org.example.aspect;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.dto.AuditEvent;
import org.example.dto.SearchCriteria;
import org.example.model.AuditAction;
import org.example.model.Product;
import org.example.service.impl.ProductServiceImpl;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/** For all methods of {@link ProductServiceImpl} publish audit event */
@Aspect
@Component
@RequiredArgsConstructor
public class AuditProductAspect {
  private static final boolean DISABLED = "true".equals(System.getProperty("aspectj.disable"));
  private final ApplicationEventPublisher eventPublisher;

  @Around("@annotation(auditProduct)")
  public Object auditProductOperation(ProceedingJoinPoint jp, AuditProduct auditProduct)
      throws Throwable {
    if (DISABLED) {
      return jp.proceed();
    }
    Object result = jp.proceed();
    String message = buildAuditMessage(auditProduct, jp.getArgs(), result);
    publish(auditProduct.action(), message);
    return result;
  }

  private String buildAuditMessage(AuditProduct auditProduct, Object[] args, Object result) {
    return switch (auditProduct.type()) {
      case SEARCH -> buildSearchMessage(args, result, auditProduct.message());
      case VIEW -> buildViewMessage(args, result, auditProduct.message());
      case ID_BASED -> buildIdBasedMessage(args, result, auditProduct.message());
      case SIMPLE -> buildSimpleMessage(auditProduct.message());
    };
  }

  private String buildSimpleMessage(String baseMessage) {
    if (baseMessage.isEmpty()) {
      return "Product operation completed";
    }
    return baseMessage;
  }

  private String buildIdBasedMessage(Object[] args, Object result, String baseMessage) {
    Optional<Long> id = extractIdFromArgsOrResult(args, result);
    if (baseMessage.isEmpty()) {
      return id.map("Product operation: [%d]"::formatted).orElse("Product operation");
    }
    return id.map(baseMessage::formatted)
        .orElse(baseMessage.replace(": [%d]", "")); // Remove ID placeholder if no ID found
  }

  private Optional<Long> extractIdFromArgsOrResult(Object[] args, Object result) {
    Optional<Long> idFromArgs = extractIdFromArgs(args);
    if (idFromArgs.isPresent()) {
      return idFromArgs;
    }
    if (result instanceof Product product) {
      return Optional.of(product.getId());
    }
    return Optional.empty();
  }

  private String buildSearchMessage(Object[] args, Object result, String baseMessage) {
    if (args.length > 0 && args[0] instanceof SearchCriteria criteria) {
      if (result instanceof List resultList) {
        return buildDetailedSearchMessage(criteria, resultList.size());
      }
    }
    if (result instanceof List resultList) {
      String defaultMessage = baseMessage.isEmpty() ? "Product search" : baseMessage;
      return defaultMessage + ", found: [%d]".formatted(resultList.size());
    }
    return baseMessage.isEmpty() ? "Product search" : baseMessage;
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

  private String buildViewMessage(Object[] args, Object result, String baseMessage) {
    if (result instanceof Optional opt && opt.isPresent() && opt.get() instanceof Product product) {
      return "Viewed product (found): [%d]".formatted(product.getId());
    }
    String defaultMessage = baseMessage.isEmpty() ? "Viewed product" : baseMessage;
    Optional<Long> productId = extractIdFromArgs(args);
    return productId
        .map(aLong -> defaultMessage + ": [%d]".formatted(aLong))
        .orElseGet(() -> defaultMessage + " without id");
  }

  private Optional<Long> extractIdFromArgs(Object[] args) {
    if (args.length > 0 && args[0] instanceof Long id) {
      return Optional.of(id);
    }
    return Optional.empty();
  }

  private void publish(AuditAction action, String message) {
    eventPublisher.publishEvent(new AuditEvent(action, message));
  }
}

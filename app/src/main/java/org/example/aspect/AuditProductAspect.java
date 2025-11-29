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

  @Around("execution(* org.example.service.impl.ProductServiceImpl.search(..))")
  public Object auditSearch(ProceedingJoinPoint jp) throws Throwable {
    if (DISABLED) {
      return jp.proceed();
    }
    Object result = jp.proceed();
    if (jp.getArgs().length > 0 && jp.getArgs()[0] instanceof SearchCriteria criteria) {
      if (result instanceof List resultList) {
        String message = buildSearchAuditMessage(criteria, resultList.size());
        publish(AuditAction.SEARCH, message);
      }
    } else {
      publish(AuditAction.SEARCH, "Product search");
    }
    return result;
  }

  private String buildSearchAuditMessage(SearchCriteria criteria, int resultCount) {
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

  private void publish(AuditAction action, String message) {
    eventPublisher.publishEvent(new AuditEvent(action, message));
  }

  @Around("execution(* org.example.service.impl.ProductServiceImpl.getAllProducts(..))")
  public Object auditGetAllProducts(ProceedingJoinPoint jp) throws Throwable {
    Object result = jp.proceed();
    if (result instanceof List resultList) {
      String message = "Product search, found: [%d]".formatted(resultList.size());
      publish(AuditAction.SEARCH, message);
    } else {
      publish(AuditAction.SEARCH, "Product search");
    }
    return result;
  }

  @Around("execution(* org.example.service.impl.ProductServiceImpl.findById(..))")
  public Object auditFindById(ProceedingJoinPoint jp) throws Throwable {
    Long productId = null;
    if (jp.getArgs().length > 0 && jp.getArgs()[0] instanceof Long id) {
      productId = id;
    }
    Object result = jp.proceed();
    if (result instanceof Optional resultOpt
        && resultOpt.isPresent()
        && resultOpt.get() instanceof Product product) {
      String message = "Viewed product (found): [%d]".formatted(product.getId());
      publish(AuditAction.VIEW_PRODUCT, message);
    }
    if (productId != null) {
      publish(AuditAction.VIEW_PRODUCT, "Viewed product: [%d]".formatted(productId));
    } else {
      publish(AuditAction.VIEW_PRODUCT, "Viewed product without id");
    }
    return result;
  }

  @Around("execution(* org.example.service.impl.ProductServiceImpl.addProduct(..))")
  public Object auditAddProduct(ProceedingJoinPoint jp) throws Throwable {
    Object result = jp.proceed();
    if (result instanceof Product product) {
      publish(AuditAction.ADD_PRODUCT, "Added product: [%d]".formatted(product.getId()));
    } else {
      publish(AuditAction.ADD_PRODUCT, "Added product");
    }
    return result;
  }

  @Around("execution(* org.example.service.impl.ProductServiceImpl.deleteProduct(..))")
  public Object auditDeleteProduct(ProceedingJoinPoint jp) throws Throwable {
    Object result = jp.proceed();
    if (jp.getArgs().length > 0 && jp.getArgs()[0] instanceof Long id) {
      publish(AuditAction.DELETE_PRODUCT, "removed product [%d]".formatted(id));
    }
    return result;
  }

  @Around("execution(* org.example.service.impl.ProductServiceImpl.updateProduct(..))")
  public Object auditUpdateProduct(ProceedingJoinPoint jp) throws Throwable {
    Object result = jp.proceed();
    if (jp.getArgs().length > 0 && jp.getArgs()[0] instanceof Long id) {
      publish(AuditAction.ADD_PRODUCT, "Updated product: [%d]".formatted(id));
    }
    return result;
  }

  @Around("execution(* org.example.service.impl.ProductServiceImpl.clearCache(..))")
  public Object auditClearCache(ProceedingJoinPoint jp) throws Throwable {
    Object result = jp.proceed();
    publish(AuditAction.CACHE_CLEAN_PRODUCT, "Cleared product cache");
    return result;
  }

  @Around("execution(* org.example.service.impl.ProductServiceImpl.create(..))")
  public Object auditCreate(ProceedingJoinPoint jp) throws Throwable {
    Object result = jp.proceed();
    if (result instanceof Product product) {
      publish(AuditAction.ADD_PRODUCT, "Created product: [%d]".formatted(product.getId()));
    } else {
      publish(AuditAction.ADD_PRODUCT, "Created product");
    }
    return result;
  }
}

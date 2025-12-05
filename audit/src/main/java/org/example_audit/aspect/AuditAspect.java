package org.example_audit.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example_audit.dto.Auditable;
import org.example_audit.service.AuditService;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;

@Aspect
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {

  private final AuditService auditService;
  private final BeanFactoryResolver beanFactoryResolver;
  private final MethodResolver methodResolver;

  @Around("@annotation(auditable)")
  public Object auditMethod(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
    Object result = joinPoint.proceed();
    Object[] args = joinPoint.getArgs();
    StandardEvaluationContext context = getContext();
    context.setVariable("result", result);
    context.setVariable("args", args);
    logAuditEvent(auditable, context);
    return result;
  }

  private void logAuditEvent(Auditable auditable, StandardEvaluationContext context) {
    try {
      ExpressionParser parser = new SpelExpressionParser();
      String message = parser.parseExpression(auditable.message()).getValue(context, String.class);
      String username = parser.parseExpression(auditable.username()).getValue(context, String.class);
      auditService.logAction(username, auditable.auditAction(), message, auditable.resource());
    } catch (Exception e) {
      log.error("Failed to log audit event", e);
    }
  }

  private StandardEvaluationContext getContext() {
    StandardEvaluationContext context = new StandardEvaluationContext();
    context.setBeanResolver(beanFactoryResolver);
    context.setMethodResolvers(List.of(methodResolver));
    return context;
  }
}

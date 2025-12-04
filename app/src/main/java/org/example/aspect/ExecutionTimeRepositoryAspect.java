package org.example.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/** For all public methods of Repository classes measure execution time and log it */
@Slf4j
@Aspect
@Component
public class ExecutionTimeRepositoryAspect {
  private static final boolean DISABLED = "true".equals(System.getProperty("aspectj.disable"));

  @Around("execution(public * org.example.repository.impl.database.*Repository.*(..))")
  public Object executionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    if (DISABLED) {
      return joinPoint.proceed();
    }
    long start = System.currentTimeMillis();
    try {
      return joinPoint.proceed();
    } finally {
      long duration = System.currentTimeMillis() - start;
      String className = joinPoint.getTarget().getClass().getSimpleName();
      String methodName = joinPoint.getSignature().getName();
      log.info("Repository method {}.{} executed in {} ms", className, methodName, duration);
    }
  }
}

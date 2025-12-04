package org.example_logging.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/** For all public methods of Repository classes measure execution time and log it */
@Slf4j
@Aspect
public class ExecutionTimeRepositoryAspect {

  @Around("@annotation(org.example_logging.annotation.WithTimingLog)")
  public Object executionTime(ProceedingJoinPoint joinPoint) throws Throwable {
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

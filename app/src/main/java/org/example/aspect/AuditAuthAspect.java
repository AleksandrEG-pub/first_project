package org.example.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.dto.AuditEvent;
import org.example.dto.LoginResult;
import org.example.model.AuditAction;
import org.example.model.User;
import org.example.service.impl.AuthServiceImpl;
import org.example.service.impl.UserContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/** For all methods of {@link AuthServiceImpl} publish audit event */
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAuthAspect {
  private static final boolean DISABLED = "true".equals(System.getProperty("aspectj.disable"));
  private final ApplicationEventPublisher eventPublisher;

  @Around("execution(* org.example.service.impl.AuthServiceImpl.*(..))")
  public Object auditAuthService(ProceedingJoinPoint jp) throws Throwable {
    if (DISABLED) {
      return jp.proceed();
    }
    Object result = jp.proceed();
    User currentUser = UserContext.getValidatedCurrentUser();
    if (result instanceof LoginResult loginResult) {
      publish(AuditAction.LOGIN, loginResult.getMessage());
    }
    if ("logout".equals(jp.getSignature().getName())) {
      if (User.anonymous() == currentUser) {
        publish(AuditAction.LOGOUT, "Logout successful");
      } else {
        publish(AuditAction.LOGOUT, "Logout failure");
      }
    }
    return result;
  }

  private void publish(AuditAction action, String message) {
    AuditEvent auditEvent = new AuditEvent(action, message);
    eventPublisher.publishEvent(auditEvent);
  }
}

package org.example.aspect;

import jakarta.annotation.PostConstruct;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.dto.AuditEvent;
import org.example.dto.LoginResult;
import org.example.model.AuditAction;
import org.example.model.User;
import org.example.service.impl.AuditEvents;
import org.example.service.impl.AuthServiceImpl;
import org.example.service.impl.UserContext;
import org.springframework.stereotype.Component;

/**
* For all methods of {@link AuthServiceImpl} publish audit event
*/
@Aspect
@Component
public class AuditAuthAspect {
  private static final boolean DISABLED = "true".equals(System.getProperty("aspectj.disable"));

  @Around("execution(* org.example.service.impl.AuthServiceImpl.*(..))")
  public Object auditAuthService(ProceedingJoinPoint jp) throws Throwable {
    if (DISABLED) {
      return jp.proceed();
    }
    Object result = jp.proceed();
    User currentUser = UserContext.getValidatedCurrentUser();
    if (result instanceof LoginResult loginResult) {
      AuditEvents.publish(new AuditEvent(AuditAction.LOGIN, loginResult.getMessage()));
    }
    if ("logout".equals(jp.getSignature().getName())) {
      if (User.anonymous() == currentUser) {
        AuditEvents.publish(new AuditEvent(AuditAction.LOGOUT, "Logout successful"));
      } else {
        AuditEvents.publish(new AuditEvent(AuditAction.LOGOUT, "Logout failure"));
      }
    }
    return result;
  }
}

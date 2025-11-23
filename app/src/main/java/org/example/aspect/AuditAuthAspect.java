package org.example.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.dto.AuditEvent;
import org.example.dto.LoginResult;
import org.example.model.AuditAction;
import org.example.model.User;
import org.example.service.impl.AuditEvents;
import org.example.service.impl.UserContext;

@Aspect
public class AuditAuthAspect {

  @Around("execution(* org.example.service.impl.AuthServiceImpl.*(..))")
  public Object auditAuthService(ProceedingJoinPoint jp) throws Throwable {
    Object result = jp.proceed();
    User currentUser = UserContext.getValidatedCurrentUser();
    System.out.println("aspect user: " + Thread.currentThread() + " " + currentUser.getUsername());
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

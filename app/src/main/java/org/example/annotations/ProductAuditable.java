package org.example.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.example_audit.dto.Auditable;
import org.example_audit.model.AuditAction;
import org.springframework.core.annotation.AliasFor;

@Auditable(username = "@auditMessageBuilder.getUsername()", resource = "product")
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProductAuditable {
  @AliasFor(annotation = Auditable.class, attribute = "auditAction")
  AuditAction auditAction() default AuditAction.CUSTOM;

  @AliasFor(annotation = Auditable.class, attribute = "message")
  String message() default "";
}

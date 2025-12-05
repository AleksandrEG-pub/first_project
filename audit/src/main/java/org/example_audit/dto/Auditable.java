package org.example_audit.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.example_audit.model.AuditAction;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Auditable {
  AuditAction auditAction() default AuditAction.CUSTOM;

  String message() default "";

  String username() default "default-username";

  String resource() default "default-resource";
}

package org.example_audit.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.example_audit.model.AuditAction;

/** Annotated method will be audited. Audit message will be created and stored in database. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Auditable {
  /** Performed action */
  AuditAction auditAction() default AuditAction.CUSTOM;

  /** Details of the action. E.g. product id, or size of found resources */
  String message() default "";

  /** who performed action */
  String username() default "default-username";

  /** Type of audited resource */
  String resource() default "default-resource";
}

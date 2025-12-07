package org.example.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.example.model.AuditAction;

/** Mark methods related to product lifecycle */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditProduct {
  /** Action performed */
  AuditAction action();

  /** Details on performed action, can be specified id, count, properties */
  String message() default "";

  /** How to display the message */
  AuditType type() default AuditType.SIMPLE;
}

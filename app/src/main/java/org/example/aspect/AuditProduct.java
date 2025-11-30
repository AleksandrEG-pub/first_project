package org.example.aspect;

import org.example.model.AuditAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditProduct {
    AuditAction action();
    String message() default "";
    AuditType type() default AuditType.SIMPLE;
}

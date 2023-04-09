package com.muchencute.biz.keycloak.validator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = NotProtectedUserOrRoleValidator.class)
@Documented
public @interface NotProtectedUserOrRole {

  String message() default "不能对 admin 账号执行该操作！";

  ResourceType resourceType();

  FieldType fieldType();

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  enum ResourceType {
    USER, ROLE
  }

  enum FieldType {
    ID, NAME
  }
}


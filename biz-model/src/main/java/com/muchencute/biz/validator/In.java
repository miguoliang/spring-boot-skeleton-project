package com.muchencute.biz.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = InValidator.class)
@Documented
public @interface In {

  String message() default "Invalid string!";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String[] set() default {};
}

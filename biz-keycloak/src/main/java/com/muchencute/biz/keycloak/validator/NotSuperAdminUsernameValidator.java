package com.muchencute.biz.keycloak.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotSuperAdminUsernameValidator implements
    ConstraintValidator<NotSuperAdminUsername, String> {

  @Override
  public void initialize(NotSuperAdminUsername constraintAnnotation) {

    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {

    return !"admin".equals(value);
  }
}

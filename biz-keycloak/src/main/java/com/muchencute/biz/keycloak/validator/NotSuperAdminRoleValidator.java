package com.muchencute.biz.keycloak.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotSuperAdminRoleValidator implements ConstraintValidator<NotSuperAdminRole, String> {

  @Override
  public void initialize(NotSuperAdminRole constraintAnnotation) {

    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {

    return !"超级管理员".equals(value);
  }
}

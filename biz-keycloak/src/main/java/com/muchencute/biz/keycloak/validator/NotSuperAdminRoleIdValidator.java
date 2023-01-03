package com.muchencute.biz.keycloak.validator;

import com.muchencute.biz.keycloak.service.KeycloakRoleService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class NotSuperAdminRoleIdValidator implements
    ConstraintValidator<NotSuperAdminRoleId, String> {

  private final String roleId;

  @Autowired
  public NotSuperAdminRoleIdValidator(KeycloakRoleService keycloakRoleService) {

    roleId = keycloakRoleService.getRole("超级管理员").getId();
  }

  @Override
  public void initialize(NotSuperAdminRoleId constraintAnnotation) {

    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {

    return !roleId.equalsIgnoreCase(value);
  }
}

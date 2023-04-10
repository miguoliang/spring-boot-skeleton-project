package com.muchencute.biz.keycloak.validator;

import com.muchencute.biz.keycloak.config.KeycloakConfig;
import com.muchencute.biz.keycloak.model.KeycloakRole;
import com.muchencute.biz.keycloak.model.UserEntity;
import com.muchencute.biz.keycloak.repository.KeycloakRoleRepository;
import com.muchencute.biz.keycloak.repository.UserEntityRepository;
import com.muchencute.biz.keycloak.service.KeycloakService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NotProtectedUserOrRoleValidator implements
  ConstraintValidator<NotProtectedUserOrRole, Object> {

  private final Set<String> PROTECTED_USERNAMES;

  private final Set<String> PROTECTED_USER_IDS;

  private final Set<String> PROTECTED_ROLE_NAMES;

  private final Set<String> PROTECTED_ROLE_IDS;

  private NotProtectedUserOrRole.FieldType fieldType;

  private NotProtectedUserOrRole.ResourceType resourceType;

  @Autowired
  public NotProtectedUserOrRoleValidator(
    KeycloakConfig keycloakConfig,
    KeycloakService keycloakService,
    UserEntityRepository userEntityRepository,
    KeycloakRoleRepository keycloakRoleRepository) {

    final var idOfRealm = keycloakService.getIdOfRealm();
    final var idOfClient = keycloakService.getIdOfClient();

    final var protectedUsers = userEntityRepository
      .findAllByUsernameInAndRealmId(keycloakConfig.getProtectedUsernames(), idOfRealm);

    final var protectedRoles = keycloakRoleRepository
      .findByNameInAndRealmIdAndClientAndClientRoleIsTrue(
        keycloakConfig.getProtectedRoleNames(),
        idOfRealm,
        idOfClient);

    PROTECTED_USERNAMES = protectedUsers.stream().map(UserEntity::getUsername).collect(Collectors.toSet());
    PROTECTED_USER_IDS = protectedUsers.stream().map(UserEntity::getId).collect(Collectors.toSet());
    PROTECTED_ROLE_NAMES = protectedRoles.stream().map(KeycloakRole::getName).collect(Collectors.toSet());
    PROTECTED_ROLE_IDS = protectedRoles.stream().map(KeycloakRole::getId).collect(Collectors.toSet());
  }

  @Override
  public void initialize(NotProtectedUserOrRole constraintAnnotation) {

    ConstraintValidator.super.initialize(constraintAnnotation);
    fieldType = constraintAnnotation.fieldType();
    resourceType = constraintAnnotation.resourceType();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {

    final var items = normalize(value);

    if (items.isEmpty()) {
      return true;
    }

    if (fieldType == NotProtectedUserOrRole.FieldType.ID && resourceType == NotProtectedUserOrRole.ResourceType.USER) {
      return items.stream().noneMatch(PROTECTED_USER_IDS::contains);
    } else if (fieldType == NotProtectedUserOrRole.FieldType.NAME && resourceType == NotProtectedUserOrRole.ResourceType.USER) {
      return items.stream().noneMatch(PROTECTED_USERNAMES::contains);
    } else if (fieldType == NotProtectedUserOrRole.FieldType.ID && resourceType == NotProtectedUserOrRole.ResourceType.ROLE) {
      return items.stream().noneMatch(PROTECTED_ROLE_IDS::contains);
    } else if (fieldType == NotProtectedUserOrRole.FieldType.NAME && resourceType == NotProtectedUserOrRole.ResourceType.ROLE) {
      return items.stream().noneMatch(PROTECTED_ROLE_NAMES::contains);
    } else {
      throw new IllegalArgumentException("不支持的类型：" + fieldType.name() + " " + resourceType.name());
    }
  }

  private Collection<String> normalize(Object value) {
    if (value == null) {
      return List.of();
    } else if (value instanceof String string) {
      return List.of(string);
    } else if (value instanceof String[] array) {
      return List.of(array);
    } else if (value instanceof Collection<?> list) {
      for (Object obj : list) {
        if (!(obj instanceof String)) {
          throw new IllegalArgumentException("不支持的类型（集合）：" + value.getClass());
        }
      }
      return list.stream().map(Object::toString).toList();
    } else {
      throw new IllegalArgumentException("不支持的类型：" + value.getClass());
    }
  }
}

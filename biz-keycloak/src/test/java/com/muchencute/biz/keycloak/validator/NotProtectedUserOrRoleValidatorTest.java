package com.muchencute.biz.keycloak.validator;

import com.muchencute.biz.keycloak.config.KeycloakConfig;
import com.muchencute.biz.keycloak.model.KeycloakRole;
import com.muchencute.biz.keycloak.model.UserEntity;
import com.muchencute.biz.keycloak.repository.KeycloakRoleRepository;
import com.muchencute.biz.keycloak.repository.UserEntityRepository;
import com.muchencute.biz.keycloak.service.KeycloakService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NotProtectedUserOrRoleValidatorTest {

  private NotProtectedUserOrRoleValidator validator;

  @BeforeAll
  void setUp() {

    final var keycloakConfig = Mockito.mock(KeycloakConfig.class);
    Mockito.when(keycloakConfig.getRealm()).thenReturn("app");
    Mockito.when(keycloakConfig.getClientId()).thenReturn("console-app");
    Mockito.when(keycloakConfig.getProtectedUsernames()).thenReturn(Set.of("jack", "mary"));
    Mockito.when(keycloakConfig.getProtectedRoleNames()).thenReturn(Set.of("admin", "super-admin"));

    final var keycloakService = Mockito.mock(KeycloakService.class);
    Mockito.when(keycloakService.getIdOfRealm()).thenReturn("1");
    Mockito.when(keycloakService.getIdOfClient()).thenReturn("2");

    final var userEntityRepository = Mockito.mock(UserEntityRepository.class);
    Mockito.when(userEntityRepository.findAllByUsernameInAndRealmId(Mockito.any(), Mockito.any()))
      .thenReturn(Set.of(mockUserEntity("1", "jack"), mockUserEntity("2", "mary")));

    final var keycloakRoleRepository = Mockito.mock(KeycloakRoleRepository.class);
    Mockito.when(keycloakRoleRepository.findByNameInAndRealmIdAndClientAndClientRoleIsTrue(Mockito.any(), Mockito.any(), Mockito.any()))
      .thenReturn(Set.of(mockKeycloakRole("3", "admin"), mockKeycloakRole("4", "super_admin")));

    final var environment = Mockito.mock(Environment.class);
    Mockito.when(environment.getActiveProfiles()).thenReturn(new String[0]);

    // Force reset static fields
    validator = new NotProtectedUserOrRoleValidator(keycloakConfig,
      keycloakService, userEntityRepository, keycloakRoleRepository, environment);
  }

  @Test
  void test_validate_username_in_protected() {

    final var annotation = Mockito.mock(NotProtectedUserOrRole.class);
    Mockito.when(annotation.fieldType()).thenReturn(NotProtectedUserOrRole.FieldType.NAME);
    Mockito.when(annotation.resourceType()).thenReturn(NotProtectedUserOrRole.ResourceType.USER);
    validator.initialize(annotation);
    assertFalse(validator.isValid("jack", null));
  }

  @Test
  void test_validate_role_name_in_protected() {

    final var annotation = Mockito.mock(NotProtectedUserOrRole.class);
    Mockito.when(annotation.fieldType()).thenReturn(NotProtectedUserOrRole.FieldType.NAME);
    Mockito.when(annotation.resourceType()).thenReturn(NotProtectedUserOrRole.ResourceType.ROLE);
    validator.initialize(annotation);
    assertFalse(validator.isValid("admin", null));
  }

  @Test
  void test_validate_username_in_protected_with_id() {

    final var annotation = Mockito.mock(NotProtectedUserOrRole.class);
    Mockito.when(annotation.fieldType()).thenReturn(NotProtectedUserOrRole.FieldType.ID);
    Mockito.when(annotation.resourceType()).thenReturn(NotProtectedUserOrRole.ResourceType.USER);
    validator.initialize(annotation);
    assertFalse(validator.isValid("1", null));
  }

  @Test
  void test_validate_role_name_in_protected_with_id() {

    final var annotation = Mockito.mock(NotProtectedUserOrRole.class);
    Mockito.when(annotation.fieldType()).thenReturn(NotProtectedUserOrRole.FieldType.ID);
    Mockito.when(annotation.resourceType()).thenReturn(NotProtectedUserOrRole.ResourceType.ROLE);
    validator.initialize(annotation);
    assertFalse(validator.isValid("3", null));
  }

  private UserEntity mockUserEntity(String id, String username) {
    final var userEntity = new UserEntity();
    userEntity.setId(id);
    userEntity.setUsername(username);
    return userEntity;
  }

  private KeycloakRole mockKeycloakRole(String id, String name) {
    final var keycloakRole = new KeycloakRole();
    keycloakRole.setId(id);
    keycloakRole.setName(name);
    return keycloakRole;
  }
}

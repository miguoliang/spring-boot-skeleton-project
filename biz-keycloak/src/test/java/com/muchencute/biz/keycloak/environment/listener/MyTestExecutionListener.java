package com.muchencute.biz.keycloak.environment.listener;

import cn.hutool.core.util.StrUtil;
import com.muchencute.biz.keycloak.environment.mock.*;
import com.muchencute.biz.keycloak.model.Group;
import com.muchencute.biz.keycloak.repository.KeycloakGroupRepository;
import com.muchencute.biz.keycloak.request.NewRoleRequest;
import com.muchencute.biz.keycloak.request.NewUserRequest;
import com.muchencute.biz.keycloak.service.KeycloakGroupService;
import com.muchencute.biz.keycloak.service.KeycloakRoleService;
import com.muchencute.biz.keycloak.service.KeycloakService;
import com.muchencute.biz.keycloak.service.KeycloakUserService;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

@Slf4j
public class MyTestExecutionListener implements TestExecutionListener, Ordered {

  @Autowired
  private KeycloakUserService keycloakUserService;

  @Autowired
  private KeycloakGroupService keycloakGroupService;

  @Autowired
  private KeycloakGroupRepository keycloakGroupRepository;

  @Autowired
  private KeycloakRoleService keycloakRoleService;

  @Autowired
  private KeycloakService keycloakService;

  @Override
  public void beforeTestClass(TestContext testContext) {

    testContext.getApplicationContext()
      .getAutowireCapableBeanFactory()
      .autowireBean(this);
  }

  @Override
  public void beforeTestMethod(@NonNull TestContext testContext) throws Exception {

    TestExecutionListener.super.beforeTestMethod(testContext);

    final var annotations = testContext.getTestMethod().getAnnotations();
    for (final var annotation : annotations) {
      actionHub(annotation);
    }
  }

  private void actionHub(Annotation annotation) {
    if (annotation instanceof MockUser mockUser) {
      newUser(mockUser);
    } else if (annotation instanceof MockUsers mockUsers) {
      Arrays.stream(mockUsers.value()).forEachOrdered(this::newUser);
    } else if (annotation instanceof MockGroup mockGroup) {
      newGroup(mockGroup);
    } else if (annotation instanceof MockGroups mockGroups) {
      Arrays.stream(mockGroups.value()).forEachOrdered(this::newGroup);
    } else if (annotation instanceof MockRole mockRole) {
      newRole(mockRole);
    } else if (annotation instanceof MockRoles mockRoles) {
      Arrays.stream(mockRoles.value()).forEachOrdered(this::newRole);
    } else if (annotation instanceof MockScope mockScope) {
      newScope(mockScope);
    } else if (annotation instanceof MockScopes mockScopes) {
      Arrays.stream(mockScopes.value()).forEachOrdered(this::newScope);
    }
  }

  private void newScope(MockScope mockScope) {

    final var scope = new ClientScopeRepresentation();
    scope.setName(mockScope.name());
    scope.setProtocol("openid-connect");
    @Cleanup final var response = keycloakService.getRealmResource().clientScopes().create(scope);
    final var scopeId = CreatedResponseUtil.getCreatedId(response);
    keycloakService.getClientResource().addDefaultClientScope(scopeId);
  }

  private void newRole(MockRole mockRole) {

    final var role = new NewRoleRequest();
    role.setName(mockRole.name());
    role.setScopes(Set.of(mockRole.scopes()));
    keycloakRoleService.newRole(role);
  }

  private void newGroup(MockGroup mockGroup) {

    final var group = new Group();
    group.setName(mockGroup.name());
    if (StrUtil.isNotBlank(mockGroup.parent())) {
      final var parent = keycloakGroupRepository.findByName(mockGroup.parent()).orElseThrow();
      group.setParentId(parent.getId());
    }
    final var createdGroup = keycloakGroupService.newGroup(group);
    log.info("newGroup: {}", createdGroup);
  }

  private void newUser(MockUser mockUser) {

    final var request = new NewUserRequest();
    request.setUsername(mockUser.username());
    request.setPassword(mockUser.password());
    final var userId = keycloakUserService.newUser(request);
    final var user = keycloakUserService.getUser(userId);
    log.info("newUser: {}", user);
  }

  @Override
  public int getOrder() {

    return Integer.MAX_VALUE;
  }
}

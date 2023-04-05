package com.muchencute.test.environment.listener;

import cn.hutool.core.util.StrUtil;
import com.muchencute.biz.keycloak.model.Group;
import com.muchencute.biz.keycloak.repository.KeycloakGroupRepository;
import com.muchencute.biz.keycloak.request.NewUserRequest;
import com.muchencute.biz.keycloak.service.KeycloakGroupService;
import com.muchencute.biz.keycloak.service.KeycloakUserService;
import com.muchencute.test.environment.mock.MockGroup;
import com.muchencute.test.environment.mock.MockGroups;
import com.muchencute.test.environment.mock.MockUser;
import com.muchencute.test.environment.mock.MockUsers;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.util.Arrays;

@Slf4j
public class MyTestExecutionListener implements TestExecutionListener, Ordered {

  @Autowired
  private KeycloakUserService keycloakUserService;

  @Autowired
  private KeycloakGroupService keycloakGroupService;

  @Autowired
  private KeycloakGroupRepository keycloakGroupRepository;

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
      if (annotation instanceof MockUser mockUser) {
        newUser(mockUser);
      } else if (annotation instanceof MockUsers mockUsers) {
        Arrays.stream(mockUsers.value()).forEachOrdered(this::newUser);
      } else if (annotation instanceof MockGroup mockGroup) {
        newGroup(mockGroup);
      } else if (annotation instanceof MockGroups mockGroups) {
        Arrays.stream(mockGroups.value()).forEachOrdered(this::newGroup);
      }
    }
  }

  void newGroup(MockGroup mockGroup) {

    final var group = new Group();
    group.setName(mockGroup.name());
    if (StrUtil.isNotBlank(mockGroup.parent())) {
      final var parent = keycloakGroupRepository.findByName(mockGroup.parent()).orElseThrow();
      group.setParentId(parent.getId());
    }
    final var createdGroup = keycloakGroupService.newGroup(group);
    log.info("newGroup: {}", createdGroup);
  }

  void newUser(MockUser mockUser) {

    final var request = new NewUserRequest();
    request.setUsername(mockUser.username());
    request.setPassword(mockUser.password());
    final var createdUser = keycloakUserService.newUser(request);
    log.info("newUser: {}", createdUser);
  }

  @Override
  public int getOrder() {

    return Integer.MAX_VALUE;
  }
}

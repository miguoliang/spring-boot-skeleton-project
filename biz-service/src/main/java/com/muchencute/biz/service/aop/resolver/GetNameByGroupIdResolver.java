package com.muchencute.biz.service.aop.resolver;

import com.muchencute.biz.keycloak.service.KeycloakGroupService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetNameByGroupIdResolver implements Resolver {

  private final KeycloakGroupService keycloakGroupService;

  @Autowired
  public GetNameByGroupIdResolver(KeycloakGroupService keycloakGroupService) {

    this.keycloakGroupService = keycloakGroupService;
  }

  @Override
  public Object getProperty(ProceedingJoinPoint joinPoint, Object proceed, String beanPath) {

    final var value = DefaultResolver.getValueFromRequestPath(beanPath);
    final var group = keycloakGroupService.getGroup(String.valueOf(value));
    return group.getName();
  }
}

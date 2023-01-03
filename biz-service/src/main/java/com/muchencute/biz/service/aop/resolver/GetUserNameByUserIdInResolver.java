package com.muchencute.biz.service.aop.resolver;

import com.muchencute.biz.keycloak.service.KeycloakService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetUserNameByUserIdInResolver implements Resolver {

  private final KeycloakService keycloakService;

  @Autowired

  public GetUserNameByUserIdInResolver(KeycloakService keycloakService) {

    this.keycloakService = keycloakService;
  }

  @Override
  public Object getProperty(ProceedingJoinPoint joinPoint, Object proceed, String beanPath) {

    final var value = DefaultResolver.getValueFromRequestPath(beanPath);
    final var userResource = keycloakService.getUserResourceById(String.valueOf(value));
    return userResource.toRepresentation().getUsername();
  }
}

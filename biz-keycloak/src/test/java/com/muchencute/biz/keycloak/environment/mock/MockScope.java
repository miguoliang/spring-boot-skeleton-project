package com.muchencute.biz.keycloak.environment.mock;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(MockScopes.class)
public @interface MockScope {

  String name();
}

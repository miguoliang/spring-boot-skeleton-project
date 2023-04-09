package com.muchencute.biz.keycloak.environment.mock;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(MockRoles.class)
public @interface MockRole {

  String name();

  String[] scopes() default {};
}

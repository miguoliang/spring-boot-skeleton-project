package com.muchencute.biz.keycloak.environment.mock;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(MockGroups.class)
public @interface MockGroup {

  String name();

  String parent() default "";
}

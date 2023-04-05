package com.muchencute.test.environment.mock;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(MockUsers.class)
public @interface MockUser {

  String username();

  String password() default "";
}


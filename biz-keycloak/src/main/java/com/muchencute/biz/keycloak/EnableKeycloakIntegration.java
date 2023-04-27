package com.muchencute.biz.keycloak;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(KeycloakIntegrationRegistrar.class)
public @interface EnableKeycloakIntegration {
  String beanNamePrefix() default "";

  String propertyPrefix() default "app.keycloak";
}

package com.muchencute.biz.keycloak.misc;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(CustomFeatureRegistrar.class)
public @interface EnableCustomFeature {

  String value() default "";
}

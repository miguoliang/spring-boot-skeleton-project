package com.muchencute.biz.service.aop.resolver;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Resolve {

  String value();

  Class<? extends Resolver> resolver() default DefaultResolver.class;
}

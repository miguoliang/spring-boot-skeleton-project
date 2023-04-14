package com.muchencute.biz.keycloak.misc;

import org.springframework.beans.factory.config.BeanPostProcessor;

public class CustomBeanPostProcessor implements BeanPostProcessor {

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) {

    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) {

    return bean;
  }
}

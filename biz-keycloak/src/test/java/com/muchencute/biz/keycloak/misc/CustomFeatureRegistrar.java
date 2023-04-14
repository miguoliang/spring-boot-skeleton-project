package com.muchencute.biz.keycloak.misc;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

@SuppressWarnings("unused")
public class CustomFeatureRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

  private ResourceLoader resourceLoader;

  private Environment environment;

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  @Override
  public void setResourceLoader(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    final var defaultAttrs = importingClassMetadata.getAnnotationAttributes(EnableCustomFeature.class.getName(), true);
    if (defaultAttrs == null) {
      return;
    }
    final var beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(Foo.class)
      .addConstructorArgValue(defaultAttrs.getOrDefault("value", "default"))
      .getBeanDefinition();
    registry.registerBeanDefinition("foo", beanDefinition);
  }
}

record Foo(String name) {

}